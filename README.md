# httpRequests
A Spigot plugin which allows player to send HTTP requests to a web server from an ingame command, and optionally receive and process the response.

Currently, only GET and POST are supported . I'll continue to update this plugin for as long as I have a use for it on my own servers, or if other people use it.

To send an HTTP request, the command format is as follows: /sendhttp (GET/POST) (address with port) (values, if any).
As an example: /sendhttp POST https://example.com:8080/pages/form_submit.php username="james"&password="1234"

For better documentation, refer to the plugin's spigot page: https://www.spigotmc.org/resources/http-requests.101253/ or check out the GitHub wiki. 
