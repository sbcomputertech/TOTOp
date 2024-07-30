# TOTOp
### (Time-based One Time Operator)


> This plugin was made for a rather specific circumstance, and not many people will probably have a need for it.
> (but it lives here anyway :3)


## Compatibility:
- Requires a spigot/paper server
- Works with minecraft 1.20 (and probably future versions)


## Usage:
- Download the latest .jar file from the releases page
- Add it to the server's plugins folder
- On start up, a QR code will be printed to the server console
- Scan the QR code with a TOTP app (e.g. google authenticator) to add the code
- On another account connected to the server, run `/totop <code>` where code is the code found in the app
- The account should have op permissions
- Use `/deopme` to de-op the account afterwards