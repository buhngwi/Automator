<?php

// Include the required libraries
require 'facebook_sdk/autoload.php';
require 'twitteroauth/autoload.php';
require 'instagram_api/instagram.php';

// Set up Facebook credentials
$fbAppId = 'YOUR_FACEBOOK_APP_ID';
$fbAppSecret = 'YOUR_FACEBOOK_APP_SECRET';
$fbAccessToken = 'YOUR_FACEBOOK_ACCESS_TOKEN';

// Set up Twitter credentials
$twitterConsumerKey = 'YOUR_TWITTER_CONSUMER_KEY';
$twitterConsumerSecret = 'YOUR_TWITTER_CONSUMER_SECRET';
$twitterAccessToken = 'YOUR_TWITTER_ACCESS_TOKEN';
$twitterAccessTokenSecret = 'YOUR_TWITTER_ACCESS_TOKEN_SECRET';

// Set up Instagram credentials
$instagramAccessToken = 'YOUR_INSTAGRAM_ACCESS_TOKEN';

// Handle form submission
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $message = $_POST['message'];
    $imageURL = $_POST['image_url'];

    // Send post to Facebook
    $fb = new Facebook\Facebook([
        'app_id' => $fbAppId,
        'app_secret' => $fbAppSecret,
        'default_access_token' => $fbAccessToken,
        'default_graph_version' => 'v13.0',
    ]);

    try {
        $fb->post('/me/feed', ['message' => $message, 'link' => $imageURL]);
        echo 'Post sent to Facebook successfully!<br>';
    } catch (Facebook\Exceptions\FacebookResponseException $e) {
        echo 'Facebook API Error: ' . $e->getMessage() . '<br>';
    } catch (Facebook\Exceptions\FacebookSDKException $e) {
        echo 'Facebook SDK Error: ' . $e->getMessage() . '<br>';
    }

    // Send post to Twitter
    $twitter = new Abraham\TwitterOAuth\TwitterOAuth($twitterConsumerKey, $twitterConsumerSecret, $twitterAccessToken, $twitterAccessTokenSecret);

    try {
        $status = $twitter->post('statuses/update', ['status' => $message . ' ' . $imageURL]);
        echo 'Post sent to Twitter successfully!<br>';
    } catch (Abraham\TwitterOAuth\TwitterOAuthException $e) {
        echo 'Twitter API Error: ' . $e->getMessage() . '<br>';
    }

    // Send post to Instagram
    try {
        $instagram = new \InstagramAPI\Instagram(false, false);
        $instagram->setUser($instagramAccessToken);
        $media = $instagram->uploadPhoto($imageURL, ['caption' => $message]);
        echo 'Post sent to Instagram successfully!<br>';
    } catch (\Exception $e) {
        echo 'Instagram API Error: ' . $e->getMessage() . '<br>';
    }
}

?>

<!DOCTYPE html>
<html>
<head>
    <title>Social Media Posting</title>
</head>
<body>
    <h1>Social Media Posting</h1>
    <form method="post">
        <label for="message">Message:</label><br>
        <textarea id="message" name="message" rows="4" cols="50"></textarea><br>

        <label for="image_url">Image URL:</label><br>
        <input type="text" id="image_url" name="image_url"><br>

        <input type="submit" value="Post">
    </form>
</body>
</html>
