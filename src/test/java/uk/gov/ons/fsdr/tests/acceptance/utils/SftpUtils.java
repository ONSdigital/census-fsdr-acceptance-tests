package uk.gov.ons.fsdr.tests.acceptance.utils;

import static uk.gov.ons.fsdr.tests.acceptance.steps.CommonSteps.gatewayEventMonitor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import lombok.extern.slf4j.Slf4j;
import uk.gov.census.ffa.storage.utils.StorageUtils;
import uk.gov.ons.census.fwmt.events.data.GatewayEventDTO;

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
    private URI sftpPrivateKey;

    @Value("${sftp.privateKeyPassphrase}")
    private String sftpPrivateKeyPassphrase;

    @Value("${sftp.pgp.csvSecretKey}")
    private URI csvSecretKey;

    @Value("${sftp.pgp.csvKeyPassphrase}")
    private String csvKeyPassword;

    @Value("${sftp.directory.hq}")
    private String hqDirectory;

    @Autowired
    private StorageUtils storageUtils;

    public String getCsv(String directory, String csvFilename) throws Exception {
        JSch jsch = new JSch();

        Session session = jsch.getSession(sftpUser, sftpHost, sftpPort);

        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);

        InputStream sftpPrivateKeyStream = storageUtils.getFileInputStream(sftpPrivateKey);
        jsch.addIdentity("name", sftpPrivateKeyStream.readAllBytes(), null, sftpPrivateKeyPassphrase.getBytes());
        sftpPrivateKeyStream.close();
        session.connect();
        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp sftp = (ChannelSftp) channel;
        InputStream is = sftp.get(directory + csvFilename);
        InputStream csvSecretKeyStream = storageUtils.getFileInputStream(csvSecretKey);
        String decryptedFile = decryptFile(csvSecretKeyStream, is, csvKeyPassword.toCharArray());
        csvSecretKeyStream.close();


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


    public String getLogisticsFileName() {
        String csvFilename = null;
        gatewayEventMonitor.hasEventTriggered("<N/A>", "LOGISTICS_EXTRACT_COMPLETE", 2000L);
       Collection<GatewayEventDTO> logistics_extract_sent = gatewayEventMonitor.grabEventsTriggered("LOGISTICS_EXTRACT_SENT", 1, 100L);
        for (GatewayEventDTO gatewayEventDTO : logistics_extract_sent) {
            csvFilename = gatewayEventDTO.getMetadata().get("logisticsFilename");
        }
        return csvFilename;
    }

    public String getLWSFileName() {
        String csvFilename = null;
        gatewayEventMonitor.hasEventTriggered("<N/A>", "LWS_EXTRACT_COMPLETE", 2000L);
        Collection<GatewayEventDTO> lws_extract_sent = gatewayEventMonitor.grabEventsTriggered("LWS_EXTRACT_SENT", 1, 100L);
        for (GatewayEventDTO gatewayEventDTO : lws_extract_sent) {
            csvFilename = gatewayEventDTO.getMetadata().get("lwsFilename");
        }
        return  csvFilename;
    }

    public String getRcaFileName() {
        String csvFilename = null;
        gatewayEventMonitor.hasEventTriggered("<N/A>", "RCA_EXTRACT_COMPLETE", 2000L);
        Collection<GatewayEventDTO> logistics_extract_sent = gatewayEventMonitor.grabEventsTriggered("RCA_EXTRACT_COMPLETE", 1, 100L);
        for (GatewayEventDTO gatewayEventDTO : logistics_extract_sent) {
            csvFilename = gatewayEventDTO.getMetadata().get("CSV Filename");
        }
        return csvFilename;
    }

    public void putFiletoSftp(String directory, String filename) throws JSchException, IOException, SftpException {
        JSch jsch = new JSch();

        Session session = jsch.getSession(sftpUser, sftpHost, sftpPort);

        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);

        InputStream sftpPrivateKeyStream = storageUtils.getFileInputStream(sftpPrivateKey);
        jsch.addIdentity("name", sftpPrivateKeyStream.readAllBytes(), null, sftpPrivateKeyPassphrase.getBytes());
        sftpPrivateKeyStream.close();
        session.connect();
        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp sftp = (ChannelSftp) channel;

        sftp.put(filename, directory);

        sftp.exit();
        session.disconnect();
    }

    public void clerarSftp() throws JSchException, IOException, SftpException {
        JSch jsch = new JSch();

        Session session = jsch.getSession(sftpUser, sftpHost, sftpPort);

        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);

        InputStream sftpPrivateKeyStream = storageUtils.getFileInputStream(sftpPrivateKey);
        jsch.addIdentity("name", sftpPrivateKeyStream.readAllBytes(), null, sftpPrivateKeyPassphrase.getBytes());
        sftpPrivateKeyStream.close();
        session.connect();
        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp sftp = (ChannelSftp) channel;

        sftp.rm(hqDirectory + "/*.csv");

        sftp.exit();
        session.disconnect();
    }
}


