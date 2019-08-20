OPDS Validor
============

This program is a specialized validator for the Open Publication Distribution System (http://http://opds-spec.org/).
It's based on Jing (http://code.google.com/p/jing-trang/).


Build
-----

On debian the dependencies are available as packages

```bash
apt install libjing-java libjson-java librelaxng-datatype-java
```

```bash
ant
```

Usage
-----

You can either point it to a OPDS feed file or pipe the feed

```bash
 usage: java -jar OPDSValidator [options] file
 Options:
 -h              This help message
 -v opds_version OPDSVersion to use (default 1.0)
 -e encoding     File encoding (passed to jing)
 -f format       Error output format (default text, avail : json)
```


