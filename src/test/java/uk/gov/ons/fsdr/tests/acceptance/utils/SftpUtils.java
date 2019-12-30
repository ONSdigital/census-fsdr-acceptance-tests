package uk.gov.ons.fsdr.tests.acceptance.utils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.jcajce.JcaPGPSecretKeyRing;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyDataDecryptorFactoryBuilder;
import org.bouncycastle.util.io.Streams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;


@Service
@Slf4j
public class SftpUtils {

    @Value("${sftp.host}")
    private String sftpHost;

    @Value("${sftp.port}")
    private int sftpPort;

    @Value("${sftp.user}")
    private String sftpUser;

    @Value("${sftp.privateKey}")
    private Resource sftpPrivateKey;

    @Value("${sftp.privateKeyPassphrase}")
    private String sftpPrivateKeyPassphrase;

    @Value("${sftp.pgp.csvSecretKey}")
    private Resource csvSecretKey;

    @Value("${sftp.pgp.csvKeyPassphrase}")
    private String csvKeyPassword;


    public String getCsv(String directory, String csvFilename) throws Exception {
        JSch jsch = new JSch();

        Session session = jsch.getSession(sftpUser, sftpHost, sftpPort);

        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);

        jsch.addIdentity("name", sftpPrivateKey.getInputStream().readAllBytes(), null, sftpPrivateKeyPassphrase.getBytes());
        session.connect();
        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp sftp = (ChannelSftp) channel;
        String decryptedFile = null;
        InputStream is = sftp.get(directory + csvFilename);
        decryptedFile = decryptFile(csvSecretKey.getInputStream(), is, csvKeyPassword.toCharArray());


        sftp.exit();
        session.disconnect();

        return decryptedFile;
    }

    private String decryptFile(InputStream secretKeyFile, InputStream file, char[] passwd) throws Exception {
        PGPPrivateKey secretKey = getSecretKey(secretKeyFile, passwd);
        PGPPublicKeyEncryptedData encryptedData = null;
        Iterator<PGPPublicKeyEncryptedData> encryptedObjects;
        encryptedObjects = getEncryptedObjects(file.readAllBytes());
        while (encryptedObjects.hasNext()) {
            encryptedData = encryptedObjects.next();
        }

        final BouncyCastleProvider provider = new BouncyCastleProvider();
        InputStream decryptedData = encryptedData.getDataStream(
                new JcePublicKeyDataDecryptorFactoryBuilder()
                        .setProvider(provider)
                        .build(secretKey));
        PGPLiteralData pgpLiteralData = asLiteral(decryptedData);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        Streams.pipeAll(pgpLiteralData.getInputStream(), out);
        return out.toString();
    }

    private static PGPLiteralData asLiteral(final InputStream clear) throws IOException, PGPException {
        final PGPObjectFactory plainFact = new PGPObjectFactory(clear, new JcaKeyFingerprintCalculator());
        final Object message = plainFact.nextObject();
        if (message instanceof PGPCompressedData) {
            final PGPCompressedData cData = (PGPCompressedData) message;
            final PGPObjectFactory pgpFact = new PGPObjectFactory(cData.getDataStream(), new JcaKeyFingerprintCalculator());
            // Find the first PGPLiteralData object
            Object object = null;
            for (int safety = 0; (safety++ < 1000) && !(object instanceof PGPLiteralData);
                 object = pgpFact.nextObject()) {
                //ignore
            }
            return (PGPLiteralData) object;
        } else if (message instanceof PGPLiteralData) {
            return (PGPLiteralData) message;
        } else {
            throw new PGPException("message is not a simple encrypted file - type unknown: " + message.getClass().getName());
        }
    }

    private PGPPrivateKey getSecretKey(InputStream pgpSecretKey, char[] password) throws IOException, PGPException {
        InputStream decoderStream = PGPUtil.getDecoderStream(pgpSecretKey);
        JcaPGPSecretKeyRing pgpSecretKeys = new JcaPGPSecretKeyRing(decoderStream);
        decoderStream.close();
        Iterator<PGPSecretKey> secretKeys = pgpSecretKeys.getSecretKeys();
        PGPPrivateKey key = null;
        while (key == null && secretKeys.hasNext()) {
            PGPSecretKey k = secretKeys.next();
            if (!k.isMasterKey() && !k.isPrivateKeyEmpty()) {
                key = k.extractPrivateKey(new JcePBESecretKeyDecryptorBuilder().setProvider(new BouncyCastleProvider()).build(password));
            }

        }
        return key;
    }

    @SuppressWarnings("unchecked")
    private static Iterator<PGPPublicKeyEncryptedData> getEncryptedObjects(final byte[] message) throws IOException {
        final PGPObjectFactory factory = new PGPObjectFactory(PGPUtil.getDecoderStream(new ByteArrayInputStream(message)), new JcaKeyFingerprintCalculator());
        final Object first = factory.nextObject();
        final Object list = (first instanceof PGPEncryptedDataList) ? first : factory.nextObject();
        return ((PGPEncryptedDataList) list).getEncryptedDataObjects();
    }
}


