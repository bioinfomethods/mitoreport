const {
  CLIENT_ID,
  ISSUER,
  OKTA_TESTING_DISABLEHTTPSCHECK,
  USE_INTERACTION_CODE,
} = process.env

let USE_INTERACTION_CODE_FLOW = false
if (USE_INTERACTION_CODE === 'true') {
  USE_INTERACTION_CODE_FLOW = true
}

export default {
  oidc: {
    clientId: '0oa8v274qtBzC3fAq4x7',
    issuer: 'https://dev-740515.okta.com/oauth2/default',
    redirectUri: `http://${window.location.host}/login/callback`,
    scopes: ['openid', 'profile', 'email'],
    pkce: true,
    useInteractionCodeFlow: USE_INTERACTION_CODE_FLOW,
    testing: {
      disableHttpsCheck: true,
    },
    cookies: {
      sameSite: 'lax',
    },
  },
}
