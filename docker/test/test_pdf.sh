# test basic pdf conversion a chinese website
curl -H "Content-Type: application/json" \
	-d '{"url":"http://hk.yahoo.com"}' \
	http://localhost:8080/pdf/convert

