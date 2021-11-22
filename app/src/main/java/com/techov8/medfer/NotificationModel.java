package com.techov8.medfer;

public class NotificationModel {

        private String image,body;
        private boolean readed;

        public NotificationModel(String image, String body, boolean readed) {
            this.image = image;
            this.body = body;
            this.readed = readed;
        }

        public String getImage() {
            return image;
        }

        public String getBody() {
            return body;
        }

        public boolean isReaded() {
            return readed;
        }

        public void setReaded(boolean readed) {
            this.readed = readed;
        }

}
