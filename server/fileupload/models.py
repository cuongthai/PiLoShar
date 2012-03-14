from google.appengine.ext import db
from google.appengine.ext import blobstore
class ImageBlob(db.Model):
	"""Models an individual Guestbook entry with an author, content, and date."""
	image_blob_key =  blobstore.BlobReferenceProperty()
	date_create = db.DateTimeProperty(auto_now_add=True)
	latitude = db.StringProperty(required=False)
	longitude = db.StringProperty(required=False)