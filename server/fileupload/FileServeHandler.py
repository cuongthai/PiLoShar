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
from google.appengine.ext.webapp import blobstore_handlers
from fileupload.models import ImageBlob
from google.appengine.api import images
import json
from google.appengine.api import memcache

class FileServeHandler(blobstore_handlers.BlobstoreDownloadHandler):
	def get(self):
		links=memcache.get("images")
		if not links:
			links=[]
			image_blobs =ImageBlob.all()
			for i in image_blobs:
				links.append(images.get_serving_url(i.image_blob_key))
			memcache.set('images',links,60*60)
		self.response.headers.add_header('Content-Type', 'application/json')
		self.response.out.write(json.dumps(links,indent=4)) 


