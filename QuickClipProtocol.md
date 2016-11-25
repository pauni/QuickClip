QCP
===

The QuickClipProtocol is just a small specification how a requst must be structured and how the response should look like.

Here are a few points:

- RESTless
- Stateless
- provide data cinsistence
- Status information (success/failed)


To fit all these needs the following protocol specifications are used:

- TCP on port 6834
- JSON encoded
- essential json fields
- consitence is done with using TCP


Server
======

Every device that wants to recieve clipboards from other devices need to start a server, called the QCdeamon. 
Also a 6-digit PIN should be randomly generated at the first start of the daemon. The client uses the PIN for authentification.

The QCdaemon listens on TCP port 6834 for incoming connections.

Requests
================================

A requst schould look like this:
```JSON
{
	"pin": "<pin>",
	"action": "<action>",
	"data": <data>
}
```
The three fields `pin`, `action` and `action` are essential fields and are necessary for a valid request.

Response
========

The response is quite simple:
```JSON
{
	"error": int,
	"message": "<message>"
}
```

`error`:

The `error` field is an integer with the an errorcode. If the request was successfull `error` is `0`. 
Otherwise it contains an other errorcode.

`message`:

Message is a human readable description for the error that _can_ be used. The client can also use their own error messages (e.g. multilang support).

Actions
=======

The Json snippets are the content of the `data` field.

__This list is NOT complete__

`sendclip`:

Sending a clip to an other device e.g.:

```JSON
{
  "clip": "this is the message i want to send",
  "md5": "92906990eb98b20c8894597417870e09",
  "mime": "text/plain"
}
```
The mimetype is given to provide special actions for the user. E.g. `text/x-url`: a link to a website.
Maybe it is also possible to send binary data. To send a jpeg we would set the mimetype to `image/jpeg`. The reciever could provide now a function like "save image to gallery".

---

More actions will follow.
Here are some ideas:

- `pinexchange`: share the pin to allow easy setup for a bidirectional clipboard sharing.
- `stillalive`: check if the server is runnig without problem.
- `wipe`: wipe the last clipboard including the history to increase privacy
- `getclip`: get the clip  from the server (if "instantsend" is disabled)
