@Acceptance
Feature: Setup

  Scenario Outline: A record is created in the downstream systems
    Given An employee exists in "<source>" with an id of "<id>"
    And an assignment status of "ASSIGNED"
    And a closing report status of "ACTIVE"
    And a role id of "<role_id>"
    And a contract start date of "2020-01-01"
    And the managers of "<role_id>" exist
    And we ingest managers
    And we ingest them
    When the employee "<id>" is sent to all downstream services
    Then the employee "<id>" is correctly created in gsuite with roleId "<role_id>"
    And the employee assignment status changes to "TRAINING IN PROGRESS"
    And we receive an update from adecco for employee "<id>" with new first name "<new_name>"
    And we ingest them
    When the employee "<id>" is sent to all downstream services
    Then the employee "<id>" is correctly setup in gsuite with orgUnit "<org_unit>" with name "<new_name>" and roleId "<role_id>"
    And the employee "<id>" is now in the current groups "<new_groups>"
     Then the employee "<id>" is correctly updated in ServiceNow with "<role_id>" and name "<new_name>" and number "<number>"
    And Check the employee "<id>" is sent to RCA
    Then the employee "<inLogisitcs>" in the Logisitics CSV with "<role_id>" and phone number "" as an update with name "<new_name>"
    Then the employee from "<source>" with roleId "<role_id>" is correctly updated in XMA with name "<new_name>" and group "<group>"
    Examples:
      | id        | role_id       | inLogisitcs | source | group                                | org_unit     | new_groups    | new_name | hier1           | hier2     | hier3 | hier4    | hier5          | hier6         | hier7        |
      | 900000001 | HA-CAR1       | is          | ADECCO | 7DD2611D-F60D-4A17-B759-B021BC5C669A | ONS Managers | ha-all,ha-mgr | John     | England & Wales | Household | A     | Carlisle | Area Manager 1 |               |              |
      | 900000002 | HA-CAR1-ZA    | is          | ADECCO | 7DD2611D-F60D-4A17-B759-B021BC5C669A | ONS Managers | ha-all,ha-mgr | John     | England & Wales | Household | A     | Carlisle | Area Manager 1 | Team Leader A |              |
      | 900000003 | HA-CAR1-ZA-01 | is not      | ADECCO | 8A2FEF60-9429-465F-B711-83753B234BDD | ONS Officers | ha-all        | John     | England & Wales | Household | A     | Carlisle | Area Manager 1 | Team Leader A | 01 Tranche 1 |

  Scenario Outline: A record is setup in gsuite with the correct groups
    Given An employee exists in "<source>" with an id of "<id>"
    And an assignment status of "ASSIGNED"
    And a closing report status of "ACTIVE"
    And a role id of "<role_id>"
    And a contract start date of "2020-01-01"
    And we ingest them
    Then the employee "<id>" is correctly created in gsuite with roleId "<role_id>"
    And the employee assignment status changes to "TRAINING IN PROGRESS"
    And we ingest them
    Then the employee "<id>" is correctly setup in gsuite with orgUnit "<org_unit>" with name "Fransico" and roleId "<role_id>"
    And the employee "<id>" is now in the current groups "<new_groups>"
    Examples:
      | id        | source | role_id       | new_groups                                   | org_unit         |
      | 900000004 | ADECCO | CA-____       | ca-all,ca-mgr                                | ONS Managers     |
      | 900000005 | ADECCO | CA-____-__    | ca-all,ca-mgr                                | ONS Managers     |
      | 900000006 | ADECCO | CA-____-__-__ | ca-all                                       | ONS CCS Officers |
      | 900000007 | ADECCO | CB-____       | cb-all,cb-mgr                                | ONS Managers     |
      | 900000008 | ADECCO | CB-____-__    | cb-all,cb-mgr                                | ONS Managers     |
      | 900000009 | ADECCO | CB-____-__-__ | cb-all                                       | ONS CCS Officers |
      | 900000010 | ADECCO | CC-____       | cc-all,cc-mgr                                | ONS Managers     |
      | 900000011 | ADECCO | CC-____-__    | cc-all,cc-mgr                                | ONS Managers     |
      | 900000012 | ADECCO | CC-____-__-__ | cc-all                                       | ONS CCS Officers |
      | 900000013 | ADECCO | CD-____       | cd-all,cd-mgr                                | ONS Managers     |
      | 900000014 | ADECCO | CD-____-__    | cd-all,cd-mgr                                | ONS Managers     |
      | 900000015 | ADECCO | CD-____-__-__ | cd-all                                       | ONS CCS Officers |
      | 900000016 | ADECCO | CE-____       | ce-all,ce-mgr                                | ONS Managers     |
      | 900000017 | ADECCO | CE-____-__    | ce-all,ce-mgr                                | ONS Managers     |
      | 900000018 | ADECCO | CE-____-__-__ | ce-all                                       | ONS CCS Officers |
      | 900000019 | ADECCO | C_-MOB_       | c-mob-all,c-mob-mgr                          | ONS Managers     |
      | 900000020 | ADECCO | C_-MOB_-__    | c-mob-all,c-mob-mgr                          | ONS Managers     |
      | 900000021 | ADECCO | C_-MOB_-__-__ | c-mob-all                                    | ONS CCS Officers |
      | 900000025 | ADECCO | HA-____       | ha-all,ha-mgr                                | ONS Managers     |
      | 900000026 | ADECCO | HA-____-__    | ha-all,ha-mgr                                | ONS Managers     |
      | 900000027 | ADECCO | HA-____-__-__ | ha-all                                       | ONS Officers     |
      | 900000028 | ADECCO | HB-____       | hb-all,hb-mgr                                | ONS Managers     |
      | 900000029 | ADECCO | HB-____-__    | hb-all,hb-mgr                                | ONS Managers     |
      | 900000030 | ADECCO | HB-____-__-__ | hb-all                                       | ONS Officers     |
      | 900000031 | ADECCO | HC-____       | hc-all,hc-mgr                                | ONS Managers     |
      | 900000032 | ADECCO | HC-____-__    | hc-all,hc-mgr                                | ONS Managers     |
      | 900000033 | ADECCO | HC-____-__-__ | hc-all                                       | ONS Officers     |
      | 900000034 | ADECCO | HD-____       | hd-all,hd-mgr                                | ONS Managers     |
      | 900000035 | ADECCO | HD-____-__    | hd-all,hd-mgr                                | ONS Managers     |
      | 900000036 | ADECCO | HD-____-__-__ | hd-all                                       | ONS Officers     |
      | 900000037 | ADECCO | HE-____       | he-all,he-mgr                                | ONS Managers     |
      | 900000038 | ADECCO | HE-____-__    | he-all,he-mgr                                | ONS Managers     |
      | 900000039 | ADECCO | HE-____-__-__ | he-all                                       | ONS Officers     |
      | 900000040 | ADECCO | HF-____       | hf-all,hf-mgr                                | ONS Managers     |
      | 900000040 | ADECCO | HF-____-__    | hf-all,hf-mgr                                | ONS Managers     |
      | 900000041 | ADECCO | HF-____-__-__ | hf-all                                       | ONS Officers     |
      | 900000042 | ADECCO | HG-____       | hg-all,hg-mgr                                | ONS Managers     |
      | 900000043 | ADECCO | HG-____-__    | hg-all,hg-mgr                                | ONS Managers     |
      | 900000044 | ADECCO | HG-____-__-__ | hg-all                                       | ONS Officers     |
      | 900000045 | ADECCO | HH-____       | hh-all,hh-mgr                                | ONS Managers     |
      | 900000046 | ADECCO | HH-____-__    | hh-all,hh-mgr                                | ONS Managers     |
      | 900000047 | ADECCO | HH-____-__-__ | hh-all                                       | ONS Officers     |
      | 900000048 | ADECCO | HJ-____       | hj-all,hj-mgr                                | ONS Managers     |
      | 900000049 | ADECCO | HJ-____-__    | hj-all,hj-mgr                                | ONS Managers     |
      | 900000050 | ADECCO | HJ-____-__-__ | hj-all                                       | ONS Managers     |
      | 900000051 | ADECCO | HK-____       | hk-all,hk-mgr                                | ONS Managers     |
      | 900000052 | ADECCO | HK-____-__    | hk-all,hk-mgr                                | ONS Managers     |
      | 900000053 | ADECCO | HK-____-__-__ | hk-all                                       | ONS Officers     |
      | 900000054 | ADECCO | HL-____       | hl-all,hl-mgr                                | ONS Managers     |
      | 900000055 | ADECCO | HL-____-__    | hl-all,hl-mgr                                | ONS Managers     |
      | 900000056 | ADECCO | HL-____-__-__ | hl-all                                       | ONS Officers     |
      | 900000057 | ADECCO | HM-____       | hm-all,hm-mgr                                | ONS Managers     |
      | 900000058 | ADECCO | HM-____-__    | hm-all,hm-mgr                                | ONS Managers     |
      | 900000059 | ADECCO | HM-____-__-__ | hm-all                                       | ONS Officers     |
      | 900000060 | ADECCO | H_-MOB_       | h-mob-all,h-mob-mgr                          | ONS Managers     |
      | 900000061 | ADECCO | H_-MOB_-__    | h-mob-all,h-mob-mgr                          | ONS Managers     |
      | 900000062 | ADECCO | H_-MOB_-__-__ | h-mob-all                                    | ONS Officers     |
      | 900000063 | ADECCO | HP-____       | hp-all,hp-mgr                                | ONS Managers     |
      | 900000064 | ADECCO | HP-____-__    | hp-all,hp-mgr                                | ONS Managers     |
      | 900000065 | ADECCO | HP-____-__-__ | hp-all                                       | ONS Officers     |
      | 900000066 | ADECCO | HR-____       | hr-all,hr-mgr                                | ONS Managers     |
      | 900000067 | ADECCO | HR-____-__    | hr-all,hr-mgr                                | ONS Managers     |
      | 900000068 | ADECCO | HR-____-__-__ | hr-all                                       | ONS Officers     |
      | 900000069 | ADECCO | HW-____       | hw-all,hw-mgr                                | ONS Managers     |
      | 900000070 | ADECCO | HW-____-__    | hw-all,hw-mgr                                | ONS Managers     |
      | 900000071 | ADECCO | HW-____-__-__ | hw-all                                       | ONS Officers     |
      | 900000073 | ADECCO | NA-____-__    | na-all,n-all,nc-apps-officer-manager         | ONS Managers     |
      | 900000074 | ADECCO | NA-____-__-__ | na-all,n-all,nc-apps-officer-manager         | ONS Officers     |
      | 900000075 | ADECCO | NA-___1       | na-mgr1,n-all,na-all,nc-apps-officer-manager | ONS Managers     |
      | 900000076 | ADECCO | NA-___2       | na-mgr2,n-all,na-all,nc-apps-officer-manager | ONS Managers     |
      | 900000077 | ADECCO | NB-____-__    | nb-all,n-all,nc-apps-officer-manager         | ONS Managers     |
      | 900000078 | ADECCO | NB-____-__-__ | nb-all,n-all,nc-apps-officer-manager         | ONS Officers     |
      | 900000079 | ADECCO | NB-___1       | nb-mgr1,n-all,nb-all,nc-apps-officer-manager | ONS Managers     |
      | 900000080 | ADECCO | NB-___2       | nb-mgr2,n-all,nb-all,nc-apps-officer-manager | ONS Managers     |
      | 900000081 | ADECCO | NC-____-__    | nc-all,n-all,nc-apps-officer-manager         | ONS Managers     |
      | 900000082 | ADECCO | NC-____-__-__ | nc-all,n-all,nc-apps-officer-manager         | ONS Officers     |
      | 900000083 | ADECCO | NC-___1       | nc-mgr1,n-all,nc-all,nc-apps-officer-manager | ONS Managers     |
      | 900000084 | ADECCO | NC-___2       | nc-mgr2,n-all,nc-all,nc-apps-officer-manager | ONS Managers     |
      | 900000085 | ADECCO | ND-____-__    | nd-all,n-all,nc-apps-officer-manager         | ONS Managers     |
      | 900000086 | ADECCO | ND-____-__-__ | nd-all,n-all,nc-apps-officer-manager         | ONS Officers     |
      | 900000087 | ADECCO | ND-___1       | nd-mgr1,n-all,nd-all,nc-apps-officer-manager | ONS Managers     |
      | 900000088 | ADECCO | ND-___2       | nd-mgr2,n-all,nd-all,nc-apps-officer-manager | ONS Managers     |
      | 900000089 | ADECCO | NE-____-__    | ne-all,n-all,nc-apps-officer-manager         | ONS Managers     |
      | 900000090 | ADECCO | NE-____-__-__ | ne-all,n-all,nc-apps-officer-manager         | ONS Officers     |
      | 900000091 | ADECCO | NE-___1       | ne-mgr1,n-all,ne-all,nc-apps-officer-manager | ONS Managers     |
      | 900000092 | ADECCO | NE-___2       | ne-mgr2,n-all,ne-all,nc-apps-officer-manager | ONS Managers     |
      | 900000093 | ADECCO | SA-____       | sa-all,sa-mg,explorer_for_arcgis             | ONS Managers     |
      | 900000094 | ADECCO | SA-____-__    | sa-all,sa-mg,explorer_for_arcgis             | ONS Managers     |
      | 900000095 | ADECCO | SA-____-__-__ | sa-all                                       | ONS CE Officers  |
      | 900000096 | ADECCO | SB-____       | sb-all,sb-mgr,explorer_for_arcgis            | ONS Managers     |
      | 900000097 | ADECCO | SB-____-__    | sb-all,sb-mgr,explorer_for_arcgis            | ONS Managers     |
      | 900000098 | ADECCO | SB-____-__-__ | sb-all                                       | ONS CE Officers  |
      | 900000099 | ADECCO | SC-____       | sc-all,sc-mgr,explorer_for_arcgis            | ONS Managers     |
      | 900000100 | ADECCO | SC-____-__    | sc-all,sc-mgr,explorer_for_arcgis            | ONS Managers     |
      | 900000101 | ADECCO | SC-____-__-__ | sc-all                                       | ONS CE Officers  |
      | 900000102 | ADECCO | SD-____       | sd-all,sd-mgr,explorer_for_arcgis            | ONS Managers     |
      | 900000103 | ADECCO | SD-____-__    | sd-all,sd-mgr,explorer_for_arcgis            | ONS Managers     |
      | 900000104 | ADECCO | SD-____-__-__ | sd-all                                       | ONS CE Officers  |
      | 900000105 | ADECCO | SE-____       | se-all,se-mgr,explorer_for_arcgis            | ONS Managers     |
      | 900000106 | ADECCO | SE-____-__    | se-all,se-mgr,explorer_for_arcgis            | ONS Managers     |
      | 900000107 | ADECCO | SE-____-__-__ | se-all                                       | ONS CE Officers  |
      | 900000108 | ADECCO | SF-____       | sf-all,sf-mgr, explorer_for_arcgis           | ONS Managers     |
      | 900000109 | ADECCO | SF-____-__    | sf-all,sf-mgr, explorer_for_arcgis           | ONS Managers     |
      | 900000100 | ADECCO | SF-____-__-__ | sf-all                                       | ONS CE Officers  |
      | 900000101 | ADECCO | SG-____       | sg-all,sg-mgr,explorer_for_arcgis            | ONS Managers     |
      | 900000102 | ADECCO | SG-____-__    | sg-all,sg-mgr,explorer_for_arcgis            | ONS Managers     |
      | 900000103 | ADECCO | SG-____-__-__ | sg-all                                       | ONS CE Officers  |
      | 900000104 | ADECCO | SH-____       | sh-all,sh-mgr,explorer_for_arcgis            | ONS Managers     |
      | 900000105 | ADECCO | SH-____-__    | sh-all,sh-mgr,explorer_for_arcgis            | ONS Managers     |
      | 900000106 | ADECCO | SH-____-__-__ | sh-all                                       | ONS CE Officers  |
      | 900000107 | ADECCO | SJ-____       | sj-all,sj-mgr,explorer_for_arcgis            | ONS Managers     |
      | 900000108 | ADECCO | SJ-____-__    | sj-all,sj-mgr,explorer_for_arcgis            | ONS Managers     |
      | 900000109 | ADECCO | SJ-____-__-__ | sj-all                                       | ONS CE Officers  |
      | 900000110 | ADECCO | SK-____       | sk-all,sk-mgr,explorer_for_arcgis            | ONS Managers     |
      | 900000111 | ADECCO | SK-____-__    | sk-all,sk-mgr,explorer_for_arcgis            | ONS Managers     |
      | 900000112 | ADECCO | SK-____-__-__ | sk-all                                       | ONS CE Officers  |
      | 900000113 | ADECCO | SL-____       | sl-all,sl-mgr,explorer_for_arcgis            | ONS Managers     |
      | 900000114 | ADECCO | SL-____-__    | sl-all,sl-mgr,explorer_for_arcgis            | ONS Managers     |
      | 900000115 | ADECCO | SL-____-__-__ | sl-all                                       | ONS CE Officers  |
      | 900000116 | ADECCO | SM-____       | sm-all,sm-mgr,explorer_for_arcgis            | ONS Managers     |
      | 900000117 | ADECCO | SM-____-__    | sm-all,sm-mgr,explorer_for_arcgis            | ONS Managers     |
      | 900000118 | ADECCO | SM-____-__-__ | sm-all                                       | ONS CE Officers  |
      | 900000119 | ADECCO | SP-____       | sp-all,sp-mgr,explorer_for_arcgis            | ONS Managers     |
      | 900000120 | ADECCO | SP-____-__    | sp-all,sp-mgr,explorer_for_arcgis            | ONS Managers     |
      | 900000121 | ADECCO | SP-____-__-__ | sp-all                                       | ONS CE Officers  |
      | 900000122 | ADECCO | SR-____       | sr-all,sr-mgr,explorer_for_arcgis            | ONS Managers     |
      | 900000123 | ADECCO | SR-____-__    | sr-all,sr-mgr,explorer_for_arcgis            | ONS Managers     |
      | 900000124 | ADECCO | SR-____-__-__ | sr-all                                       | ONS CE Officers  |
      | 900000125 | ADECCO | SW-____       | sw-all,sw-mgr,explorer_for_arcgis            | ONS Managers     |
      | 900000126 | ADECCO | SW-____-__    | sw-all,sw-mgr,explorer_for_arcgis            | ONS Managers     |
      | 900000127 | ADECCO | SW-____-__-__ | sw-all                                       | ONS CE Officers  |
      | 900000128 | ADECCO | XA-____       | xa-all,xa-mgr,x-cem-mgr                      | ONS CEM and CA   |
      | 900000129 | ADECCO | XA-____-__    | xa-all,x-cem-adv                             | ONS CEM and CA   |
      | 900000130 | ADECCO | XA-____-__-__ | xa-all,x-cem-adv                             | ONS CEM and CA   |
      | 900000131 | ADECCO | XB-____       | xb-all,xb-mgr,x-cem-mgr                      | ONS CEM and CA   |
      | 900000132 | ADECCO | XB-____-__    | xb-all,x-cem-adv                             | ONS CEM and CA   |
      | 900000133 | ADECCO | XB-____-__-__ | xb-all,x-cem-adv                             | ONS CEM and CA   |
      | 900000134 | ADECCO | XC-____       | xc-all,xc-mgr,x-cem-mgr                      | ONS CEM and CA   |
      | 900000135 | ADECCO | XC-____-__    | xc-all,x-cem-adv                             | ONS CEM and CA   |
      | 900000136 | ADECCO | XC-____-__-__ | xc-all,x-cem-adv                             | ONS CEM and CA   |
      | 900000137 | ADECCO | XD-____       | xd-all,xd-mgr,x-cem-mgr                      | ONS CEM and CA   |
      | 900000138 | ADECCO | XD-____-__    | xd-all,x-cem-adv                             | ONS CEM and CA   |
      | 900000139 | ADECCO | XD-____-__-__ | xd-all,x-cem-adv                             | ONS CEM and CA   |
      | 900000140 | ADECCO | XE-____       | xe-all,xe-mgr,x-cem-mgr                      | ONS CEM and CA   |
      | 900000141 | ADECCO | XE-____-__    | xe-all,x-cem-adv                             | ONS CEM and CA   |
      | 900000142 | ADECCO | XE-____-__-__ | xe-all,x-cem-adv                             | ONS CEM and CA   |
      | 900000143 | ADECCO | XF-____       | xf-all,xf-mgr,x-cem-mgr                      | ONS CEM and CA   |
      | 900000144 | ADECCO | XF-____-__    | xf-all,x-cem-adv                             | ONS CEM and CA   |
      | 900000145 | ADECCO | XF-____-__-__ | xf-all,x-cem-adv                             | ONS CEM and CA   |
      | 900000146 | ADECCO | XG-____       | xg-all,xg-mgr,x-cem-mgr                      | ONS CEM and CA   |
      | 900000147 | ADECCO | XG-____-__    | xg-all,x-cem-adv                             | ONS CEM and CA   |
      | 900000148 | ADECCO | XG-____-__-__ | xg-all,x-cem-adv                             | ONS CEM and CA   |
      | 900000149 | ADECCO | XH-____       | xh-all,xh-mgr,x-cem-mgr                      | ONS CEM and CA   |
      | 900000150 | ADECCO | XH-____-__    | xh-all,x-cem-adv                             | ONS CEM and CA   |
      | 900000151 | ADECCO | XH-____-__-__ | xh-all,x-cem-adv                             | ONS CEM and CA   |
      | 900000152 | ADECCO | XJ-____       | xj-all,xj-mgr,x-cem-mgr                      | ONS CEM and CA   |
      | 900000153 | ADECCO | XJ-____-__    | xj-all,x-cem-adv                             | ONS CEM and CA   |
      | 900000154 | ADECCO | XJ-____-__-__ | xj-all,x-cem-adv                             | ONS CEM and CA   |
      | 900000155 | ADECCO | XK-____       | xk-all,xk-mgr,x-cem-mgr                      | ONS CEM and CA   |
      | 900000156 | ADECCO | XK-____-__    | xk-all,x-cem-adv                             | ONS CEM and CA   |
      | 900000157 | ADECCO | XK-____-__-__ | xk-all,x-cem-adv                             | ONS CEM and CA   |
      | 900000158 | ADECCO | XL-____       | xl-all,xl-mgr,x-cem-mgr                      | ONS CEM and CA   |
      | 900000159 | ADECCO | XL-____-__    | xl-all,x-cem-adv                             | ONS CEM and CA   |
      | 900000160 | ADECCO | XL-____-__-__ | xl-all,x-cem-adv                             | ONS CEM and CA   |
      | 900000161 | ADECCO | XM-____       | xm-all,xm-mgr,x-cem-mgr                      | ONS CEM and CA   |
      | 900000162 | ADECCO | XM-____-__    | xm-all,x-cem-adv                             | ONS CEM and CA   |
      | 900000163 | ADECCO | XM-____-__-__ | xm-all,x-cem-adv                             | ONS CEM and CA   |
      | 900000164 | ADECCO | X_-MOB_       | x-mob-all                                    | ONS CEM and CA   |
      | 900000165 | ADECCO | X_-MOB_-__    | x-mob-all                                    | ONS CEM and CA   |
      | 900000166 | ADECCO | X_-MOB-__-__  | x-mob-all                                    | ONS CEM and CA   |
      | 900000167 | ADECCO | XP-____       | xp-all,xp-mgr,x-cem-mgr                      | ONS CEM and CA   |
      | 900000168 | ADECCO | XP-____-__    | xp-all,x-cem-adv                             | ONS CEM and CA   |
      | 900000169 | ADECCO | XP-____-__-__ | xp-all,x-cem-adv                             | ONS CEM and CA   |
      | 900000170 | ADECCO | XR-____       | xr-all,xr-mgr,x-cem-mgr                      | ONS CEM and CA   |
      | 900000171 | ADECCO | XR-____-__    | xr-all,x-cem-adv                             | ONS CEM and CA   |
      | 900000172 | ADECCO | XR-____-__-__ | xr-all,x-cem-adv                             | ONS CEM and CA   |
      | 900000173 | ADECCO | XW-____       | xw-all,xw-mgr,x-cem-mgr                      | ONS CEM and CA   |
      | 900000174 | ADECCO | XW-____-__    | xw-all,x-cem-adv                             | ONS CEM and CA   |
      | 900000175 | ADECCO | XW-____-__-__ | xw-all,x-cem-adv                             | ONS CEM and CA   |