package com.qunli.gitbase;

public class MediaInfo {

    /**
     * media_platform_tiny_id : 144115223275594142
     * media_platform_info_meeting : {"media_gw_ip":"41.95.126.118","media_gw_port":"8000","media_gw_token":"ljUjZ95a5dr21ekF145KglUuhvfIoXYSNpi1pm0sn6xQVJxQRig+lODMNwkiI6JrHg6ewmtPrbrp5On1FDTjIVlDePpdBqncUg6YuVwPdSJNYS+IzsvkaCesnTshcWUw7edOgh34RNwDBOg0YQ9sHMnrQCqw6z2UXhHhUm6d6bcWngUEFoXuVFCSiwCtzwbSw9bEYC6aaWNvZuJ2xMnC2kAnQZxblC63bSrnm6dMpSHj987hMAM6WsdBC+PmgjxULqNDZRTRm7bwBL+WuvF8vmG8ZEdS8jvdX5oIY17JPAY0U8Ee3b8G3P/l2o30Q7plQdxv/NQBw3gHXNjGoE6IlOwdQ/v+rzMkfW0RrIV7NTamRGJ291/OkARNGJNzBJfmfSEtPanJKgM4fkjjU3d7Hf/ZGJTU69qnCD47f8oIMNgXFz1SAwOgpO8DuvSCjhC9rf+x4qGu3HNar+O/XOkkHRv32vDgaXbYaIPBRPXnPaFl2wuDvdy1PoNsYYmtzwMv4PA2e8BBzqcMRr71K8LBkcWoq9bU+uJLL9bkRc1gsZKv4yiDNjsJroX02GJp8Jg6D4ete408Z7P1ybJomz6iK82UEXR6V93EhP8iA/aCp5KZ9wcpgKBxx/aTwxvS3li3IvsADLyYKKYHJmmRt2uf3HcVqJXxzgOcC+q6esFyqpnFD1sxVhN+tfB2vqqzc/LMDp6/BJPt9ZccYw06shKFOBAzBExrd3GgMjpHq6rSZfsqTU9CjJw2DbFXqk+yhOnOrBbBYLfsrC4="}
     * public_multi_screen_setting : {"screen_layout":1}
     * control_permission : {"audio":{"allow_up_stream":true,"allow_down_stream":true},"share_screen":{"allow_up_stream":true,"allow_down_stream":true},"video":{"allow_up_stream":true,"allow_down_stream":true}}
     * control_permission_meeting : {"audio":{"allow_up_stream":true,"allow_down_stream":true},"share_screen":{"allow_up_stream":true,"allow_down_stream":true},"video":{"allow_up_stream":true,"allow_down_stream":true}}
     * init_media_config : {"audio":{"switch_state":0},"video":{"switch_state":0}}
     * open_id : 3_test001_200000037
     * av_feature_switches : 13607802
     * is_second_in : false
     * creator_sdk_appid : 1400143280
     */

    private String media_platform_tiny_id;
    private MediaPlatformInfoMeetingBean media_platform_info_meeting;
    private PublicMultiScreenSettingBean public_multi_screen_setting;
    private ControlPermissionBean control_permission;
    private ControlPermissionMeetingBean control_permission_meeting;
    private InitMediaConfigBean init_media_config;
    private String open_id;
    private int av_feature_switches;
    private boolean is_second_in;
    private String creator_sdk_appid;

    public String getMedia_platform_tiny_id() {
        return media_platform_tiny_id;
    }

    public void setMedia_platform_tiny_id(String media_platform_tiny_id) {
        this.media_platform_tiny_id = media_platform_tiny_id;
    }

    public MediaPlatformInfoMeetingBean getMedia_platform_info_meeting() {
        return media_platform_info_meeting;
    }

    public void setMedia_platform_info_meeting(MediaPlatformInfoMeetingBean media_platform_info_meeting) {
        this.media_platform_info_meeting = media_platform_info_meeting;
    }

    public PublicMultiScreenSettingBean getPublic_multi_screen_setting() {
        return public_multi_screen_setting;
    }

    public void setPublic_multi_screen_setting(PublicMultiScreenSettingBean public_multi_screen_setting) {
        this.public_multi_screen_setting = public_multi_screen_setting;
    }

    public ControlPermissionBean getControl_permission() {
        return control_permission;
    }

    public void setControl_permission(ControlPermissionBean control_permission) {
        this.control_permission = control_permission;
    }

    public ControlPermissionMeetingBean getControl_permission_meeting() {
        return control_permission_meeting;
    }

    public void setControl_permission_meeting(ControlPermissionMeetingBean control_permission_meeting) {
        this.control_permission_meeting = control_permission_meeting;
    }

    public InitMediaConfigBean getInit_media_config() {
        return init_media_config;
    }

    public void setInit_media_config(InitMediaConfigBean init_media_config) {
        this.init_media_config = init_media_config;
    }

    public String getOpen_id() {
        return open_id;
    }

    public void setOpen_id(String open_id) {
        this.open_id = open_id;
    }

    public int getAv_feature_switches() {
        return av_feature_switches;
    }

    public void setAv_feature_switches(int av_feature_switches) {
        this.av_feature_switches = av_feature_switches;
    }

    public boolean isIs_second_in() {
        return is_second_in;
    }

    public void setIs_second_in(boolean is_second_in) {
        this.is_second_in = is_second_in;
    }

    public String getCreator_sdk_appid() {
        return creator_sdk_appid;
    }

    public void setCreator_sdk_appid(String creator_sdk_appid) {
        this.creator_sdk_appid = creator_sdk_appid;
    }

    public static class MediaPlatformInfoMeetingBean {
        /**
         * media_gw_ip : 41.95.126.118
         * media_gw_port : 8000
         * media_gw_token : ljUjZ95a5dr21ekF145KglUuhvfIoXYSNpi1pm0sn6xQVJxQRig+lODMNwkiI6JrHg6ewmtPrbrp5On1FDTjIVlDePpdBqncUg6YuVwPdSJNYS+IzsvkaCesnTshcWUw7edOgh34RNwDBOg0YQ9sHMnrQCqw6z2UXhHhUm6d6bcWngUEFoXuVFCSiwCtzwbSw9bEYC6aaWNvZuJ2xMnC2kAnQZxblC63bSrnm6dMpSHj987hMAM6WsdBC+PmgjxULqNDZRTRm7bwBL+WuvF8vmG8ZEdS8jvdX5oIY17JPAY0U8Ee3b8G3P/l2o30Q7plQdxv/NQBw3gHXNjGoE6IlOwdQ/v+rzMkfW0RrIV7NTamRGJ291/OkARNGJNzBJfmfSEtPanJKgM4fkjjU3d7Hf/ZGJTU69qnCD47f8oIMNgXFz1SAwOgpO8DuvSCjhC9rf+x4qGu3HNar+O/XOkkHRv32vDgaXbYaIPBRPXnPaFl2wuDvdy1PoNsYYmtzwMv4PA2e8BBzqcMRr71K8LBkcWoq9bU+uJLL9bkRc1gsZKv4yiDNjsJroX02GJp8Jg6D4ete408Z7P1ybJomz6iK82UEXR6V93EhP8iA/aCp5KZ9wcpgKBxx/aTwxvS3li3IvsADLyYKKYHJmmRt2uf3HcVqJXxzgOcC+q6esFyqpnFD1sxVhN+tfB2vqqzc/LMDp6/BJPt9ZccYw06shKFOBAzBExrd3GgMjpHq6rSZfsqTU9CjJw2DbFXqk+yhOnOrBbBYLfsrC4=
         */

        private String media_gw_ip;
        private String media_gw_port;
        private String media_gw_token;

        public String getMedia_gw_ip() {
            return media_gw_ip;
        }

        public void setMedia_gw_ip(String media_gw_ip) {
            this.media_gw_ip = media_gw_ip;
        }

        public String getMedia_gw_port() {
            return media_gw_port;
        }

        public void setMedia_gw_port(String media_gw_port) {
            this.media_gw_port = media_gw_port;
        }

        public String getMedia_gw_token() {
            return media_gw_token;
        }

        public void setMedia_gw_token(String media_gw_token) {
            this.media_gw_token = media_gw_token;
        }
    }

    public static class PublicMultiScreenSettingBean {
        /**
         * screen_layout : 1
         */

        private int screen_layout;

        public int getScreen_layout() {
            return screen_layout;
        }

        public void setScreen_layout(int screen_layout) {
            this.screen_layout = screen_layout;
        }
    }

    public static class ControlPermissionBean {
        /**
         * audio : {"allow_up_stream":true,"allow_down_stream":true}
         * share_screen : {"allow_up_stream":true,"allow_down_stream":true}
         * video : {"allow_up_stream":true,"allow_down_stream":true}
         */

        private AudioBean audio;
        private ShareScreenBean share_screen;
        private VideoBean video;

        public AudioBean getAudio() {
            return audio;
        }

        public void setAudio(AudioBean audio) {
            this.audio = audio;
        }

        public ShareScreenBean getShare_screen() {
            return share_screen;
        }

        public void setShare_screen(ShareScreenBean share_screen) {
            this.share_screen = share_screen;
        }

        public VideoBean getVideo() {
            return video;
        }

        public void setVideo(VideoBean video) {
            this.video = video;
        }

        public static class AudioBean {
            /**
             * allow_up_stream : true
             * allow_down_stream : true
             */

            private boolean allow_up_stream;
            private boolean allow_down_stream;

            public boolean isAllow_up_stream() {
                return allow_up_stream;
            }

            public void setAllow_up_stream(boolean allow_up_stream) {
                this.allow_up_stream = allow_up_stream;
            }

            public boolean isAllow_down_stream() {
                return allow_down_stream;
            }

            public void setAllow_down_stream(boolean allow_down_stream) {
                this.allow_down_stream = allow_down_stream;
            }
        }

        public static class ShareScreenBean {
            /**
             * allow_up_stream : true
             * allow_down_stream : true
             */

            private boolean allow_up_stream;
            private boolean allow_down_stream;

            public boolean isAllow_up_stream() {
                return allow_up_stream;
            }

            public void setAllow_up_stream(boolean allow_up_stream) {
                this.allow_up_stream = allow_up_stream;
            }

            public boolean isAllow_down_stream() {
                return allow_down_stream;
            }

            public void setAllow_down_stream(boolean allow_down_stream) {
                this.allow_down_stream = allow_down_stream;
            }
        }

        public static class VideoBean {
            /**
             * allow_up_stream : true
             * allow_down_stream : true
             */

            private boolean allow_up_stream;
            private boolean allow_down_stream;

            public boolean isAllow_up_stream() {
                return allow_up_stream;
            }

            public void setAllow_up_stream(boolean allow_up_stream) {
                this.allow_up_stream = allow_up_stream;
            }

            public boolean isAllow_down_stream() {
                return allow_down_stream;
            }

            public void setAllow_down_stream(boolean allow_down_stream) {
                this.allow_down_stream = allow_down_stream;
            }
        }
    }

    public static class ControlPermissionMeetingBean {
        /**
         * audio : {"allow_up_stream":true,"allow_down_stream":true}
         * share_screen : {"allow_up_stream":true,"allow_down_stream":true}
         * video : {"allow_up_stream":true,"allow_down_stream":true}
         */

        private AudioBeanX audio;
        private ShareScreenBeanX share_screen;
        private VideoBeanX video;

        public AudioBeanX getAudio() {
            return audio;
        }

        public void setAudio(AudioBeanX audio) {
            this.audio = audio;
        }

        public ShareScreenBeanX getShare_screen() {
            return share_screen;
        }

        public void setShare_screen(ShareScreenBeanX share_screen) {
            this.share_screen = share_screen;
        }

        public VideoBeanX getVideo() {
            return video;
        }

        public void setVideo(VideoBeanX video) {
            this.video = video;
        }

        public static class AudioBeanX {
            /**
             * allow_up_stream : true
             * allow_down_stream : true
             */

            private boolean allow_up_stream;
            private boolean allow_down_stream;

            public boolean isAllow_up_stream() {
                return allow_up_stream;
            }

            public void setAllow_up_stream(boolean allow_up_stream) {
                this.allow_up_stream = allow_up_stream;
            }

            public boolean isAllow_down_stream() {
                return allow_down_stream;
            }

            public void setAllow_down_stream(boolean allow_down_stream) {
                this.allow_down_stream = allow_down_stream;
            }
        }

        public static class ShareScreenBeanX {
            /**
             * allow_up_stream : true
             * allow_down_stream : true
             */

            private boolean allow_up_stream;
            private boolean allow_down_stream;

            public boolean isAllow_up_stream() {
                return allow_up_stream;
            }

            public void setAllow_up_stream(boolean allow_up_stream) {
                this.allow_up_stream = allow_up_stream;
            }

            public boolean isAllow_down_stream() {
                return allow_down_stream;
            }

            public void setAllow_down_stream(boolean allow_down_stream) {
                this.allow_down_stream = allow_down_stream;
            }
        }

        public static class VideoBeanX {
            /**
             * allow_up_stream : true
             * allow_down_stream : true
             */

            private boolean allow_up_stream;
            private boolean allow_down_stream;

            public boolean isAllow_up_stream() {
                return allow_up_stream;
            }

            public void setAllow_up_stream(boolean allow_up_stream) {
                this.allow_up_stream = allow_up_stream;
            }

            public boolean isAllow_down_stream() {
                return allow_down_stream;
            }

            public void setAllow_down_stream(boolean allow_down_stream) {
                this.allow_down_stream = allow_down_stream;
            }
        }
    }

    public static class InitMediaConfigBean {
        /**
         * audio : {"switch_state":0}
         * video : {"switch_state":0}
         */

        private AudioBeanXX audio;
        private VideoBeanXX video;

        public AudioBeanXX getAudio() {
            return audio;
        }

        public void setAudio(AudioBeanXX audio) {
            this.audio = audio;
        }

        public VideoBeanXX getVideo() {
            return video;
        }

        public void setVideo(VideoBeanXX video) {
            this.video = video;
        }

        public static class AudioBeanXX {
            /**
             * switch_state : 0
             */

            private int switch_state;

            public int getSwitch_state() {
                return switch_state;
            }

            public void setSwitch_state(int switch_state) {
                this.switch_state = switch_state;
            }
        }

        public static class VideoBeanXX {
            /**
             * switch_state : 0
             */

            private int switch_state;

            public int getSwitch_state() {
                return switch_state;
            }

            public void setSwitch_state(int switch_state) {
                this.switch_state = switch_state;
            }
        }
    }
}
