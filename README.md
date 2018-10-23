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
	<td>/api/registration/validate_name?name={value}</td>
	<td></td>
	<td><pre>"OK|INVALID|ALREADY_USED"</pre></td>
	<td>No</td>
</tr>

<tr>
	<td>GET</td>
	<td>/api/registration/validate_email?email={value}</td>
	<td></td>
	<td><pre>"OK|INVALID|ALREADY_USED"</pre></td>
	<td>No</td>
</tr>

<tr>
	<td>GET</td>
	<td>/api/registration/validate_username?username={value}</td>
	<td></td>
	<td><pre>"OK|INVALID|ALREADY_USED"</pre></td>
	<td>No</td>
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
    <td>No</td>
</tr>

<tr>
	<td>GET</td>
	<td>/api/feed?sort_by={top|new}&offset={number}</td>
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
        "vote": "UPVOTE|DOWNVOTE|NONE",
        "comments": number,
        "subjeddit": {
            "id": number,
            "name": string,
            "image": string
        },
        "poster": {
            "id": number,
            "username": string
        }
    }
    ...
]</pre></td>
    <td>Yes</td>
</tr>

<tr>
	<td>GET</td>
	<td>/api/post/{subjeddit_name}/{random_id}/{title_id}</td>
	<td></td>
	<td><pre>{
    "id": number,
    "created_at": number,
    "updated_at": number?,
    "random_id": string,
    "title_id": string,
    "title": string,
    "text": string,
    "points": number,
    "vote": "UPVOTE|DOWNVOTE|NONE",
    "comments": number,
    "subjeddit": {
        "id": number,
        "name": string,
        "image": string
    },
    "poster": {
        "id": number,
        "username": string
    }
}</pre></td>
    <td>Optional (All votes will be "NONE")</td>
</tr>

<tr>
	<td>GET</td>
	<td>/api/post/{id}/comments</td>
	<td></td>
	<td><pre>[
    {
        "created_at": number,
        "updated_at": number,
        "text": string,
        "points": number,
        "vote": "UPVOTE|DOWNVOTE|NONE",
        "user": {
            "id": number,
            "username": string
        },
        "replies": [
            ...
        ]
    }
    ...
]</pre></td>
    <td>Optional (All votes will be "NONE")</td>
</tr>

<tr>
	<td>POST</td>
	<td>/api/post/{id}/comments</td>
	<td><pre>{
    "text": string,
    "parent_comment_id": number?
}</pre></td>
    <td></td>
    <td>Yes</td>
</tr>

<tr>
	<td>GET</td>
	<td>/api/subjeddit/{name}/info</td>
	<td></td>
	<td><pre>{
    "id": number,
    "name": string,
    "image": string,
    "description": string,
    "subscribed": boolean,
    "subscribers": number
}</pre></td>
    <td>Optional ("subscribed" will be false)</td>
</tr>

<tr>
	<td>GET</td>
	<td>/api/subjeddit/{name}/posts?sort_by={top|new}&offset={number}</td>
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
        "vote": "UPVOTE|DOWNVOTE|NONE",
        "comments": number,
        "subjeddit": {
            "id": number,
            "name": string,
            "image": string
        },
        "poster": {
            "id": number,
            "username": string
        }
    }
    ...
]</pre></td>
    <td>Optional (All votes will be "NONE")</td>
</tr>

<tr>
	<td>POST</td>
	<td>/api/subjeddit/{name}/subscribe</td>
	<td><pre></pre></td>
    <td></td>
    <td>Yes</td>
</tr>

<tr>
	<td>POST</td>
	<td>/api/subjeddit/{name}/unsubscribe</td>
	<td><pre></pre></td>
    <td></td>
    <td>Yes</td>
</tr>

<tr>
	<td>GET</td>
	<td>/api/user/{username}/posts?sort_by={top|new}&offset={number}</td>
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
        "vote": "UPVOTE|DOWNVOTE|NONE",
        "comments": number,
        "subjeddit": {
            "id": number,
            "name": string,
            "image": string
        },
        "poster": {
            "id": number,
            "username": string
        }
    }
    ...
]</pre></td>
    <td>Optional (All votes will be "NONE")</td>
</tr>

<tr>
	<td>GET</td>
	<td>/api/user/{username}/comments</td>
	<td></td>
	<td><pre>[
    {
        "created_at": number
        "updated_at": number
        "text": string
        "points": number
        "user": {
            "id": number,
            "username": string
        }
        "post": {
            "id": number,
            "title": string,
            "subjeddit": {
                "id": number,
                "name": string
            },
            "poster": {
                "id": number,
                "username": string
            }
        }
    }
    ...
]</pre></td>
    <td>No</td>
</tr>

<tr>
	<td>GET</td>
	<td>/api/user/{username}/info</td>
	<td><pre></pre></td>
    <td><pre>{
    "username": string,
    "first_name": string,
    "last_name": string    
}</pre></td>
    <td>No</td>
</tr>

<tr>
	<td>GET</td>
	<td>/api/user/me/info</td>
	<td><pre></pre></td>
    <td><pre>{
    "username": string,
    "first_name": string,
    "last_name": string    
}</pre></td>
    <td>Yes</td>
</tr>

<tr>
	<td>POST</td>
	<td>/api/vote/post</td>
	<td><pre>{
    "id": number,
    "vote_type": "UPVOTE|DOWNVOTE|NONE"
}</pre></td>
    <td></td>
    <td>Yes</td>
</tr>

<tr>
	<td>POST</td>
	<td>/api/vote/comment</td>
	<td><pre>{
    "id": number,
    "vote_type": "UPVOTE|DOWNVOTE|NONE"
}</pre></td>
    <td></td>
    <td>Yes</td>
</tr>

</table>