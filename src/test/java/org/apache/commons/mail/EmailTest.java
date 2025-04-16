package org.apache.commons.mail;

import static org.junit.Assert.*;
import java.util.Date;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EmailTest {
    private static final String[] TEST_EMAILS = {"ab@bc.com", "a.b@c.org", "abcdefghijk@abdcdefghijk.com.bd"};
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_NAME = "Test User";
    private static final String TEST_HOST_NAME = "smtp.example.com";
    private static final String TEST_HEADER_NAME = "X-Priority";
    private static final String TEST_HEADER_VALUE = "1";
    private static final int TEST_TIMEOUT = 3000;

    private EmailConcrete email;

    @Before
    public void setUpEmailTest() throws Exception {
        email = new EmailConcrete();
    }

    @After
    public void tearDownEmailTest() throws Exception {
        // Cleanup resources if needed
    }

    /* Test addBcc(String... emails) */
    @Test
    public void testAddBcc() throws Exception {
        email.addBcc(TEST_EMAILS);
        assertEquals(3, email.getBccAddresses().size());
    }

    /* Test addCc(String email) */
    @Test
    public void testAddCc() throws Exception {
        email.addCc(TEST_EMAIL);
        assertEquals(1, email.getCcAddresses().size());
        assertEquals(TEST_EMAIL, email.getCcAddresses().get(0).getAddress());
    }

    /* Test addHeader(String name, String value) */
    @Test
    public void testAddHeaderValid() {
        email.addHeader(TEST_HEADER_NAME, TEST_HEADER_VALUE);
        assertTrue(email.headers.containsKey(TEST_HEADER_NAME));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddHeaderInvalidName() {
        email.addHeader("", TEST_HEADER_VALUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddHeaderInvalidValue() {
        email.addHeader(TEST_HEADER_NAME, "");
    }

    /* Test addReplyTo(String email, String name) */
    @Test
    public void testAddReplyTo() throws Exception {
        email.addReplyTo(TEST_EMAIL, TEST_NAME);
        assertEquals(1, email.getReplyToAddresses().size());
        
        InternetAddress address = email.getReplyToAddresses().get(0);
        assertEquals(TEST_EMAIL, address.getAddress());
        assertEquals(TEST_NAME, address.getPersonal());
    }

    /* Test buildMimeMessage() */
    @Test(expected = EmailException.class)
    public void testBuildMimeMessageMissingFrom() throws Exception {
        email.addTo(TEST_EMAIL);
        email.buildMimeMessage();
    }

    @Ignore("Disabled for CI – requires valid mail server")
    @Test
    public void testBuildMimeMessageSuccess() throws Exception {
        email.setFrom(TEST_EMAIL);
        email.addTo(TEST_EMAIL);
        email.setSubject("Test Subject");
        email.setContent("Test Content", "text/plain");
        email.buildMimeMessage();
        assertNotNull(email.getMimeMessage());
    }

    /* Test getHostName() */
    @Test
    public void testGetHostName() {
        email.setHostName(TEST_HOST_NAME);
        assertEquals(TEST_HOST_NAME, email.getHostName());
    }

    /* Test getMailSession() */
    @Test
    public void testGetMailSession() throws Exception {
        email.setHostName(TEST_HOST_NAME);
        Session session = email.getMailSession();
        assertNotNull(session);
        assertEquals(TEST_HOST_NAME, session.getProperty(EmailConstants.MAIL_HOST));
    }

    /* Test getSentDate() */
    @Test
    public void testGetSentDate() {
        Date testDate = new Date();
        email.setSentDate(testDate);
        assertEquals(testDate, email.getSentDate());
    }

    /* Test getSocketConnectionTimeout() */
    @Test
    public void testGetSocketConnectionTimeout() {
        email.setSocketConnectionTimeout(TEST_TIMEOUT);
        assertEquals(TEST_TIMEOUT, email.getSocketConnectionTimeout());
    }

    /* Test setFrom(String email) */
    @Test
    public void testSetFrom() throws Exception {
        email.setFrom(TEST_EMAIL);
        assertEquals(TEST_EMAIL, email.getFromAddress().getAddress());
    }

    /* Concrete test implementation */
    private static class EmailConcrete extends Email {
        @Override
        public Email setMsg(String msg) throws EmailException {
            this.setContent(msg, EmailConstants.TEXT_PLAIN);
            return this;
        }
    }
}