import tkinter as tk
from tkinter import messagebox
import requests
import tweepy
from instabot import Bot

# Set up API credentials for Facebook, Twitter, and Instagram
FACEBOOK_ACCESS_TOKEN = 'YOUR_FACEBOOK_ACCESS_TOKEN'
TWITTER_CONSUMER_KEY = 'YOUR_TWITTER_CONSUMER_KEY'
TWITTER_CONSUMER_SECRET = 'YOUR_TWITTER_CONSUMER_SECRET'
TWITTER_ACCESS_TOKEN = 'YOUR_TWITTER_ACCESS_TOKEN'
TWITTER_ACCESS_TOKEN_SECRET = 'YOUR_TWITTER_ACCESS_TOKEN_SECRET'
INSTAGRAM_USERNAME = 'YOUR_INSTAGRAM_USERNAME'
INSTAGRAM_PASSWORD = 'YOUR_INSTAGRAM_PASSWORD'

# Create the GUI application
def send_post():
    message = message_entry.get()
    image_url = image_url_entry.get()

    # Send post to Facebook
    try:
        response = requests.post(
            f'https://graph.facebook.com/me/feed?access_token={FACEBOOK_ACCESS_TOKEN}',
            json={'message': message, 'link': image_url}
        )
        response.raise_for_status()
        messagebox.showinfo('Facebook', 'Post sent to Facebook successfully!')
    except requests.exceptions.RequestException as e:
        messagebox.showerror('Facebook', f'Error sending post to Facebook: {str(e)}')

    # Send post to Twitter
    try:
        auth = tweepy.OAuthHandler(TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET)
        auth.set_access_token(TWITTER_ACCESS_TOKEN, TWITTER_ACCESS_TOKEN_SECRET)
        api = tweepy.API(auth)
        api.update_status(f'{message} {image_url}')
        messagebox.showinfo('Twitter', 'Post sent to Twitter successfully!')
    except tweepy.TweepError as e:
        messagebox.showerror('Twitter', f'Error sending post to Twitter: {str(e)}')

    # Send post to Instagram
    try:
        bot = Bot()
        bot.login(username=INSTAGRAM_USERNAME, password=INSTAGRAM_PASSWORD)
        bot.upload_photo(image_url, caption=message)
        messagebox.showinfo('Instagram', 'Post sent to Instagram successfully!')
    except Exception as e:
        messagebox.showerror('Instagram', f'Error sending post to Instagram: {str(e)}')

# Create the main window
window = tk.Tk()
window.title('Social Media Posting')
window.geometry('400x200')

# Create labels and entry fields
message_label = tk.Label(window, text='Message:')
message_label.pack()
message_entry = tk.Entry(window, width=40)
message_entry.pack()

image_url_label = tk.Label(window, text='Image URL:')
image_url_label.pack()
image_url_entry = tk.Entry(window, width=40)
image_url_entry.pack()

# Create the post button
post_button = tk.Button(window, text='Post', command=send_post)
post_button.pack()

# Start the main loop
window.mainloop()
