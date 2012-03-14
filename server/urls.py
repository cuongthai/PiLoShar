# -*- coding: utf-8 -*-
"""URL definitions."""
import webapp2

routes = [
    (r'/upload_image/','fileupload.FileuploadHandler.FileuploadHandler'),
	(r'/all_images/','fileupload.FileServeHandler.FileServeHandler'),
]

