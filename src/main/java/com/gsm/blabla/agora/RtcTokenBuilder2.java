package com.gsm.blabla.agora;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RtcTokenBuilder2 {
    public enum Role {
        ROLE_PUBLISHER(1),
        ROLE_SUBSCRIBER(2),
        ;

        public int initValue;

        Role(int initValue) {
            this.initValue = initValue;
        }
    }

    public String buildTokenWithUid(String appId, String appCertificate, String channelName, Long uid, Role role, int token_expire, int privilege_expire) {
        return buildTokenWithUserAccount(appId, appCertificate, channelName, AccessToken2.getUidStr(uid), role, token_expire, privilege_expire);
    }

    public String buildTokenWithUserAccount(String appId, String appCertificate, String channelName, String account, Role role, int token_expire, int privilege_expire) {
        AccessToken2 accessToken = new AccessToken2(appId, appCertificate, token_expire);
        AccessToken2.Service serviceRtc = new AccessToken2.ServiceRtc(channelName, account);

        serviceRtc.addPrivilegeRtc(AccessToken2.PrivilegeRtc.PRIVILEGE_JOIN_CHANNEL, privilege_expire);
        if (role == Role.ROLE_PUBLISHER) {
            serviceRtc.addPrivilegeRtc(AccessToken2.PrivilegeRtc.PRIVILEGE_PUBLISH_AUDIO_STREAM, privilege_expire);
            serviceRtc.addPrivilegeRtc(AccessToken2.PrivilegeRtc.PRIVILEGE_PUBLISH_VIDEO_STREAM, privilege_expire);
            serviceRtc.addPrivilegeRtc(AccessToken2.PrivilegeRtc.PRIVILEGE_PUBLISH_DATA_STREAM, privilege_expire);
        }
        accessToken.addService(serviceRtc);

        try {
            return accessToken.build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }

    public String buildTokenWithUid(String appId, String appCertificate, String channelName, long uid,
        int tokenExpire, int joinChannelPrivilegeExpire, int pubAudioPrivilegeExpire,
        int pubVideoPrivilegeExpire, int pubDataStreamPrivilegeExpire) {
        return buildTokenWithUserAccount(appId, appCertificate, channelName, AccessToken2.getUidStr(uid),
            tokenExpire, joinChannelPrivilegeExpire, pubAudioPrivilegeExpire, pubVideoPrivilegeExpire, pubDataStreamPrivilegeExpire);
    }

    public String buildTokenWithUserAccount(String appId, String appCertificate, String channelName, String account,
        int tokenExpire, int joinChannelPrivilegeExpire, int pubAudioPrivilegeExpire,
        int pubVideoPrivilegeExpire, int pubDataStreamPrivilegeExpire) {
        AccessToken2 accessToken = new AccessToken2(appId, appCertificate, tokenExpire);
        AccessToken2.Service serviceRtc = new AccessToken2.ServiceRtc(channelName, account);

        serviceRtc.addPrivilegeRtc(AccessToken2.PrivilegeRtc.PRIVILEGE_JOIN_CHANNEL, joinChannelPrivilegeExpire);
        serviceRtc.addPrivilegeRtc(AccessToken2.PrivilegeRtc.PRIVILEGE_PUBLISH_AUDIO_STREAM, pubAudioPrivilegeExpire);
        serviceRtc.addPrivilegeRtc(AccessToken2.PrivilegeRtc.PRIVILEGE_PUBLISH_VIDEO_STREAM, pubVideoPrivilegeExpire);
        serviceRtc.addPrivilegeRtc(AccessToken2.PrivilegeRtc.PRIVILEGE_PUBLISH_DATA_STREAM, pubDataStreamPrivilegeExpire);
        accessToken.addService(serviceRtc);

        try {
            return accessToken.build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }
}
