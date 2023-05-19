import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.requests.media.MediaConfigureToStoryRequest;
import com.github.instagram4j.instagram4j.requests.media.MediaConfigureTimelineRequest;
import com.github.instagram4j.instagram4j.requests.media.MediaConfigureToStoryRequest.MediaConfigureToStoryPayload;
import com.github.instagram4j.instagram4j.requests.media.MediaConfigureTimelineRequest.MediaConfigurePayload;
import com.github.instagram4j.instagram4j.requests.media.MediaUploadRequest;
import com.github.instagram4j.instagram4j.requests.payload.StatusResult;

public class SocialMediaPostingApp extends JFrame {
    private JTextField messageField;
    private JTextField imageField;

    public SocialMediaPostingApp() {
        setTitle("Social Media Posting");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        JLabel messageLabel = new JLabel("Message:");
        messageField = new JTextField();

        JLabel imageLabel = new JLabel("Image URL:");
        imageField = new JTextField();

        JButton postButton = new JButton("Post");
        postButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = messageField.getText();
                String imageURL = imageField.getText();

                // Send post to Facebook
                try {
                    CloseableHttpClient httpClient = HttpClients.createDefault();
                    HttpPost request = new HttpPost("https://graph.facebook.com/me/feed?access_token=YOUR_FACEBOOK_ACCESS_TOKEN");
                    String json = String.format("{\"message\":\"%s\",\"link\":\"%s\"}", message, imageURL);
                    StringEntity entity = new StringEntity(json);
                    request.setEntity(entity);
                    request.setHeader("Content-Type", "application/json");
                    httpClient.execute(request);
                    httpClient.close();
                    JOptionPane.showMessageDialog(null, "Post sent to Facebook successfully!");
                } catch (IOException ex) {
                    Logger.getLogger(SocialMediaPostingApp.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(null, "Error sending post to Facebook: " + ex.getMessage());
                }

                // Send post to Twitter
                try {
                    ConfigurationBuilder cb = new ConfigurationBuilder();
                    cb.setDebugEnabled(true)
                            .setOAuthConsumerKey("YOUR_TWITTER_CONSUMER_KEY")
                            .setOAuthConsumerSecret("YOUR_TWITTER_CONSUMER_SECRET")
                            .setOAuthAccessToken("YOUR_TWITTER_ACCESS_TOKEN")
                            .setOAuthAccessTokenSecret("YOUR_TWITTER_ACCESS_TOKEN_SECRET");

                    TwitterFactory tf = new TwitterFactory(cb.build());
                    Twitter twitter = tf.getInstance();
                    Status status = twitter.updateStatus(message + " " + imageURL);
                    JOptionPane.showMessageDialog(null, "Post sent to Twitter successfully!");
                } catch (TwitterException ex) {
                    Logger.getLogger(SocialMediaPostingApp.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(null, "Error sending post to Twitter: " + ex.getMessage());
                }

                // Send post to Instagram
                try {
                    IGClient client = IGClient.builder()
                            .username("YOUR_INSTAGRAM_USERNAME")
                            .password("YOUR_INSTAGRAM_PASSWORD")
                            .login();
                    MediaUploadRequest uploadRequest = new MediaUploadRequest(new java.io.File(imageURL));
                    StatusResult uploadResult = client.sendRequest(uploadRequest);

                    MediaConfigurePayload configurePayload = new MediaConfigurePayload();
                    configurePayload.upload_id = uploadResult.getUpload_id();
                    configurePayload.caption = message;
                    MediaConfigureTimelineRequest configureRequest = new MediaConfigureTimelineRequest(configurePayload);
                    client.sendRequest(configureRequest);

                    MediaConfigureToStoryPayload configureToStoryPayload = new MediaConfigureToStoryPayload();
                    configureToStoryPayload.upload_id = uploadResult.getUpload_id();
                    configureToStoryPayload.caption = message;
                    MediaConfigureToStoryRequest configureToStoryRequest = new MediaConfigureToStoryRequest(configureToStoryPayload);
                    client.sendRequest(configureToStoryRequest);

                    JOptionPane.showMessageDialog(null, "Post sent to Instagram successfully!");
                } catch (IOException ex) {
                    Logger.getLogger(SocialMediaPostingApp.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(null, "Error sending post to Instagram: " + ex.getMessage());
                }
            }
        });

        panel.add(messageLabel);
        panel.add(messageField);
        panel.add(imageLabel);
        panel.add(imageField);
        panel.add(new JLabel()); // Empty label for spacing
        panel.add(postButton);

        add(panel);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SocialMediaPostingApp();
            }
        });
    }
}
