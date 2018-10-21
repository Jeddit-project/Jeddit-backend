# Endpoints:

<table>
<tr>
	<th>Method</th>
	<th>URL</th>
	<th>Request</th>
	<th>Response</th>
	<th>Token required</th>
</tr>

<tr>
	<td>POST</td>
	<td>/auth</td>
	<td><pre>{
	"username": string,
	"password": string
}</pre></td>	
	<td><pre>{
	"token": string
}</pre></td>
	<td>No</td>
</tr>

<tr>
	<td>GET</td>
	<td>/api/registration/validate_name?name=`value`</td>
	<td></td>
	<td><pre>"OK|INVALID|ALREADY_USED"</pre></td>
</tr>

<tr>
	<td>GET</td>
	<td>/api/registration/validate_email?email=`value`</td>
	<td></td>
	<td><pre>"OK|INVALID|ALREADY_USED"</pre></td>
</tr>

<tr>
	<td>GET</td>
	<td>/api/registration/validate_username?username=`value`</td>
	<td></td>
	<td><pre>"OK|INVALID|ALREADY_USED"</pre></td>
</tr>

<tr>
	<td>POST</td>
	<td>/api/registration/register</td>
	<td><pre>{
	"first_name": string,
	"last_name": string,
	"email": string,
	"username": string,
	"password": string,
	"confirmation_password": string
}</pre></td>	
	<td><pre>{
	"success": boolean,
	"token": string?
}</pre></td>
</tr>

<tr>
	<td>GET</td>
	<td>/api/feed</td>
	<td></td>
	<td><pre>[
             	{
             		"id": number,
             		"created_at": number,
             		"updated_at": number?,
             		"random_id": string,
             		"title_id": string,
             		"title": string,
             		"text": string,
             		"points": number,
             		"vote": "UPVOTE|DOWNVOTE|null",
             		"comments": number,
             		"subjeddit": {
             		}
             	}
             ]</pre></td>
</tr>

</table>


### GET(token) /api/feed


Query string parameters:

* ` sort_by=top|new`
* `offset` Gets the next 20 posts from this number (0 to get the first posts)

Response:
```
[
	{
		"id": number,
		"created_at": number,
		"updated_at": number?,

		"random_id": string,
		"title_id": string,
		
		"title": string,
		"text": string,
		"points": number,
		"vote": "UPVOTE|DOWNVOTE|null",
		"comments": number,

		"subjeddit": {

		}
	}
]
```


### GET()