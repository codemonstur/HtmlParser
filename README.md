
[![Build Status](https://travis-ci.org/codemonstur/htmlparser.svg?branch=master)](https://travis-ci.org/codemonstur/htmlparser)
[![GitHub Release](https://img.shields.io/github/release/codemonstur/htmlparser.svg)](https://github.com/codemonstur/htmlparser/releases) 
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.codemonstur/htmlparser/badge.svg)](http://mvnrepository.com/artifact/com.github.codemonstur/htmlparser)
[![Maintainability](https://api.codeclimate.com/v1/badges/63924c44946973cb37f8/maintainability)](https://codeclimate.com/github/codemonstur/htmlparser/maintainability)
[![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/dwyl/esta/issues)
[![Coverage Status](https://coveralls.io/repos/github/codemonstur/htmlparser/badge.svg?branch=master)](https://coveralls.io/github/codemonstur/htmlparser?branch=master)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/813d8482256b4ed88e2ff1018d53f06e)](https://www.codacy.com/app/codemonstur/htmlparser)
[![Sputnik](https://sputnik.ci/conf/badge)](https://sputnik.ci/app#/builds/codemonstur/htmlparser)
[![MIT Licence](https://badges.frapsoft.com/os/mit/mit.svg?v=103)](https://opensource.org/licenses/mit-license.php)

# HtmlParser

A project for parsing HTML.
Currently the code doesn't work.
All I did was copy over the SimplXml project code and delete everything having to do with serialization.
And then I renamed XML to HTML.

Real world HTML is more messy.
I'll need to get some blobs of HTML and start running tests.

## How to use

Not yet.
But feel free to experiment if you are bold.

## How to get

Eventually I'll put it on maven central.
For now you'll have to check out the code and build locally.

    git clone https://github.com/codemonstur/htmlparser.git
    cd htmlparser
    mvn package

## License

The MIT license.
And I left out the copyright notice everywhere because I just don't care.

## Known issues

I wrote a test to throw the parser at some HTML code in the wild.
It failed miserably.

Obvious stuff that needs to be fixed:
- The parser doesn't understand inline <script>'s
- Autoclosing tags such as <br> and <hr> are not supported
- Comments are automatically filtered out

The script tag thing is the most egregious.
Everybody has inline scripts and most of those have < and > symbols in them.
