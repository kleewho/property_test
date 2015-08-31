# property-test

A Clojure library designed to ... to test new query parser against the old one with
input generated automatically.

## Usage

Just for now I have generator:

```clojure
(gen/sample full-broadcast-query-gen) =>
("" "" "start>2015-08-31T11:40:29Z" "end=2015-08-31T14:40:29Z&field=recordLink&sort=imi"
"end>=2015-08-31T10:40:29Z&sort=imi" "end=2015-08-31T11:40:29Z&limit=3" ""
"offset=5" "limit=0&field=channel.genres,recordLink,channel.genres,id,channel.synopsis,statistics&field=twitterInfo,channel.synopsis,imi&field=channel.selfLink,statistics,channel.name,imi,id&end<2015-08-31T12:40:29Z&field=id,id&field=channel.synopsis"
"sort=id&sort=channel.genres&start<=2015-08-31T13:40:29Z&field=channel.selfLink,channel.logoLink&limit=8")

```

## License

Copyright © 2015 Łukasz Klich

Distributed under: do what you want with it. It's just a toy.
