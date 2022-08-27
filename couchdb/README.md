# Configuring CouchDB

CouchDB is an optional component of Mitoreport.  This document describes how it can be configured with Okta, MCRI's IDP
provider.  When integrated with Okta and CouchDB, Mitoreport cannot be run as a local 

## Getting Started

Start CouchDB, ENV vars `COUCHDB_USER` and `COUCHDB_PASSWORD` should be overriden to set a custom admin password. Create
own copy of `.env` from `env.template`.

```bash
# cd to mitoreport root directory
docker-compose up -d
```

Login to CouchDB admin console at `http://127.0.0.1:5984/_utils/#/setup` with credentials
`$COUCHDB_USER:$COUCHDB_PASSWORD` to complete setup.

## Appendix

### Configuring CouchDB `[jwt_keys]` in PEM from Okta /v1/keys

The public key from Okta is required by CouchDB `[jwt_keys]` to validate JWT ID Tokens used for authentication.  This is
required in PEM format but the key from Okta ("$OKTA_ISSUER_URL/v1/keys") is json.  Use below JS snippet to convert.

```js
var jwkToPem = require('jwk-to-pem'), jwt = require('jsonwebtoken');

var idToken = "<paste minted ID Token here>";

// Returned from "$OKTA_ISSUER_URL/oauth2/v1/keys"
var jwk = {
  "kty": "RSA",
  "alg": "RS256",
  "kid": "h_h5de1DQmNEKkJautYF_tOSjgnT9I6Awl7deUydTak",
  "use": "sig",
  "e": "AQAB",
  "n": "oj23uku28SCPOHylB-MEhw39WOTNItWfPy6o2B3vDHnoqaqtHcV2TralAyNk9Psvq_bOOdl5gzPTmtqgCo66WHDDOP_yxeeANWTzmiXzpCKAqvCcRoGK4lR-rHb1Lnk49A-rXu1EQtm_fWcxObcKNFPayIchJvMfqIPN5CMxAdThcQ-xfRFLf6YRm-Ydrp6sPAYnbZ70Sws9mM9Be4omJWCCSV1mKHcwsCxfkFLx9Ji2yeNyxFn4-WJG8dKVZRv8WxjxFN9jNNUKWpICQJcAK1nYVO3X90lBdi-mfW5eAlryn3fgmUiZac8QTH09hRA4ODVMdygSyNcRTS461Z__4w"
}

pem = jwkToPem(jwk);
jwt.verify(idToken, pem);
```

### Mint Tokens From Okta Using Step CLI

Download and install [step-cli](https://smallstep.com/docs/step-cli/installation)

```bash
OKTA_ISSUER_URL="https://dev-740515.okta.com/oauth2/default"
OKTA_CLIENT_ID="0oa8v274qtBzC3fAq4x7"
RESOURCE_SERVER_URL="http://localhost:5984"

# Run below and continue login in browser
step oauth \
  --provider $OKTA_ISSUER_URL \
  --client-id $OKTA_CLIENT_ID \
  --listen 0.0.0.0:10000 --listen-url http://127.0.0.1:10000 \
  --scope="openid profile email" \
  --bare --oidc
```

### OIDC IDP Metadata

```bash
OKTA_ISSUER_URL="https://dev-740515.okta.com/oauth2/default"
curl -L "$OKTA_ISSUER_URL/.well-known/openid-configuration" | jq '.'
```

### Use curl and Admin Credentials to Query CouchDB

```bash
BASIC_AUTH=$(echo -n "$COUCHDB_USER:$COUCHDB_PASSWORD" | openssl base64)
curl -H 'content-type: application/json; charset=utf-8' -H "Authorization: Basic $BASIC_AUTH" "http://localhost:5984/_all_dbs"
```
