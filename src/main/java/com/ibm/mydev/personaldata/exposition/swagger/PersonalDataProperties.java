package com.ibm.mydev.personaldata.exposition.swagger;

import org.springframework.boot.context.properties.ConfigurationProperties;
import springfox.documentation.service.Contact;

@ConfigurationProperties(prefix = "persdata")
public class PersonalDataProperties {

    private final Swagger swagger = new Swagger();

    public Swagger getSwagger() {
        return swagger;
    }

    public static class Swagger {

        private String title = "UXRH HR_PERSONAL_DATA API";
        private String description = "REST API tho expose content to authorized BNPP applications";
        private String version;
        private String termsOfServiceUrl;
        private Contact contact = new Contact("team", null, "team@test.com");
        private String license;
        private String licenseUrl;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getTermsOfServiceUrl() {
            return termsOfServiceUrl;
        }

        public void setTermsOfServiceUrl(String termsOfServiceUrl) {
            this.termsOfServiceUrl = termsOfServiceUrl;
        }

        public Contact getContact() {
            return contact;
        }

        public void setContact(Contact contact) {
            this.contact = contact;
        }

        public String getLicense() {
            return license;
        }

        public void setLicense(String license) {
            this.license = license;
        }

        public String getLicenseUrl() {
            return licenseUrl;
        }

        public void setLicenseUrl(String licenseUrl) {
            this.licenseUrl = licenseUrl;
        }
    }

}