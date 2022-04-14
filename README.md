# httpRequests
A Spigot plugin which allows player to send HTTP requests to a web server from an ingame command.

Currently, I've only tested GET and POST. The plugin does not allow data to be recieved from the web server, as this could be a security risk for some servers, though i may be willing to add this feature in some way should people wish. I'll continue to update this plugin for as long as I have a use for it on my own servers, or if other people use it.

To send an HTTP request, the command format is as follows: /sendhttp (GET/POST) (address with port) (values, if any).
As an example: /sendhttp POST https://example.com:8080/pages/form_submit.php username=james&password=1234

For better documentation, refer to the plugin's spigot page: https://www.spigotmc.org/resources/http-requests.101253/
