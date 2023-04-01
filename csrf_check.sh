# Voer de HTTP HEAD-request uit en sla de response op in een variabele
# response=$(http HEAD localhost:9090/api/bla)
response=$(http --headers HEAD localhost:9090/api/bla)

echo "response: $response"

# Gebruik reguliere expressies om de waarden van JSESSIONID en XSRF-TOKEN te extraheren
regex_jsessionId="Set-Cookie: JSESSIONID=([^;]+)"
regex_xsrftoken="Set-Cookie: XSRF-TOKEN=([^;]+)"

if [[ $response =~ $regex_jsessionId ]]; then
  # Als JSESSIONID gevonden is, sla de waarde op in een variabele
  jsessionId="${BASH_REMATCH[1]}"
else
  # Als JSESSIONID niet gevonden is, geef een foutmelding
  echo "JSESSIONID not found in response headers"
fi

if [[ $response =~ $regex_xsrftoken ]]; then
  # Als XSRF-TOKEN gevonden is, sla de waarde op in een variabele
  xsrfToken="${BASH_REMATCH[1]}"
else
  # Als XSRF-TOKEN niet gevonden is, geef een foutmelding
  echo "XSRF-TOKEN not found in response headers"
fi

# Print de waarden van JSESSIONID en XSRF-TOKEN
echo "JSESSIONID: $jsessionId"
echo "XSRF-TOKEN: $xsrfToken"
