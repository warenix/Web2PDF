# test basic pdf conversion a chinese website
curl -H "Content-Type: application/json" \
	-d '{"url":"http://hk.yahoo.com"}' \
	http://localhost:5000/pdf/convert

