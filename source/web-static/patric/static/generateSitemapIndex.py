#!/usr/bin/python
import os
import sys
import datetime

# setting default encoding to utf-8
reload(sys)
sys.setdefaultencoding("UTF-8")

outfile = open("sitemapindex_features.xml", "wb")

print >>outfile, """<?xml version="1.0" encoding="UTF-8"?>
<sitemapindex xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">"""

# read directory listing
    
for fileName in os.listdir("./sitemaps/"):
    
    print >>outfile, """    <sitemap>
        <loc>http://patricbrc.org/patric/static/sitemaps/%s</loc>
        <lastmod>%s</lastmod>
    </sitemap>""" % (fileName, datetime.date.fromtimestamp(os.path.getmtime("./sitemaps/"+fileName)))
    
print >>outfile, "</sitemapindex>"
