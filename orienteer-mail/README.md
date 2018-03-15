## orienteer-mail
Simply send E-mail with Orienteer.

## Guide for creating data
1. Configure class OMailSettings
- set unique email
- set email password
- set SMTP host (for Gmail it is `smtp.gmail.com`)
- set SMTP port (for Gmail it is `587` for TLS and `465` for SSL)
- set SMTP TLS/SSL (for Gmail it must be `true`)
2. Configure class OMail
- set unique name
- set subject
- set from
- set text (text sends as HTML)
- set link to OMailSettings class which will use for sending this mail

#### NOTE
One document OMailSettings can used for many documents OMail. 

## Guide for sending email
1. Inject IOMailService to you class with Google Guice
```java
    @com.google.inject.Inject
    private IOMailService mailService;
```
2. Create or get from database class OMail
3. Simply call one of methods `IOMailService`
- `IOMailService.sendMail(to, mail)` - blocking send mail to recipient. Can throws exception.
- `IOMailService.sendMailAsync(to, mail)` - asynchronous send mail to recipient. Don't throws exception.
- `IOMailService.sendMailAsync(to, mail, callback)` - asynchronous send mail to recipient. Don't throws exception.
Calls callback after mail send.