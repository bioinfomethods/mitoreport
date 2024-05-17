import Keycloak from 'keycloak-js'

const keycloak = new Keycloak({
  url: 'https://keycloak.mcri.edu.au:8888',
  realm: 'bioinfomethods-test',
  clientId: 'archie-native-test',
})

keycloak.sub = () => {
  return keycloak.idTokenParsed.sub
}

keycloak.preferred_username = () => {
  return keycloak.idTokenParsed.preferred_username
}

keycloak.email = () => {
  return keycloak.idTokenParsed.email
}

keycloak.given_name = () => {
  return keycloak.idTokenParsed.given_name
}

keycloak.family_name = () => {
  return keycloak.idTokenParsed.family_name
}

keycloak.initials = () => {
  if (keycloak.idTokenParsed?.name) {
    const name = keycloak.idTokenParsed.name
    return name
      .split(' ')
      .map(n => n[0])
      .join('')
  }

  return 'AA'
}

keycloak.groups = () => {
  return keycloak.idTokenParsed.groups
}

keycloak.ad_groups = () => {
  return keycloak.idTokenParsed.ad_groups
}

export default keycloak

// Example of keycloak.idTokenParsed:
// {
//   "exp": 1715772271,
//   "iat": 1715770471,
//   "auth_time": 1715770470,
//   "jti": "9ff8e348-c7a9-4be1-945a-6fd96c36cb01",
//   "iss": "https://keycloak.mcri.edu.au:8888/realms/bioinfomethods-test",
//   "aud": [
//       "archie-native-test",
//       "archietest"
//   ],
//   "sub": "b31e385c-621f-4dff-b1ef-b511e487a725",
//   "typ": "ID",
//   "azp": "archie-native-test",
//   "nonce": "31b9fbd2-7984-4f8c-b9e5-693e813e7dc6",
//   "session_state": "464365d5-b9d0-47fb-9192-6430082b74d0",
//   "at_hash": "grOWz_iPrDyG4aigvwoBdw",
//   "acr": "1",
//   "sid": "464365d5-b9d0-47fb-9192-6430082b74d0",
//   "ad_groups": [
//       "ad_group_1",
//       "ad_group_2",
//   ],
//   "resource_access": {
//       "account": {
//           "roles": [
//               "manage-account",
//               "manage-account-links",
//               "view-profile"
//           ]
//       }
//   },
//   "email_verified": true,
//   "realm_access": {
//       "roles": [
//           "default-roles-bioinfomethods-test",
//           "offline_access",
//           "uma_authorization"
//       ]
//   },
//   "name": "Tommy Li",
//   "groups": [
//       "bioinfomethods_test_group"
//   ],
//   "preferred_username": "tommy.li@mcri.edu.au",
//   "given_name": "Tommy",
//   "family_name": "Li",
//   "email": "tommy.li@mcri.edu.au"
// }
