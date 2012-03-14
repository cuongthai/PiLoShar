#!/usr/bin/env python
#
# Copyright 2007 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
import cgi
import datetime
import urllib
import webapp2

from google.appengine.ext import blobstore
from fileupload.models import ImageBlob

class FileuploadHandler(webapp2.RequestHandler):
	def get(self):
		self.response.out.write(blobstore.create_upload_url('/upload_image'))
	def post(self):
		upload = self.get_uploads()[0]
		latitude = self.request.POST.get('latitude')
		longitude = self.request.POST.get('longitude')
		image = ImageBlob(image_blob_key=upload.key(),latitude=latitude,longitude=longitude)
		db.put(image)
		self.response.out.write(image.pk)


