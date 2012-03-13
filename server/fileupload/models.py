from google.appengine.ext import db
class Greeting(db.Model):
	"""Models an individual Guestbook entry with an author, content, and date."""
	image_file = db.UserProperty()
	date_create = db.DateTimeProperty(auto_now_add=True)
	latitude = db.StringProperty(multiline=True)
	longitude = db.StringProperty(multiline=True)