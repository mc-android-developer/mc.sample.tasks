package mc.sample.tasks.xing.model;

class Owner {
    final String html_url;
    final String login;

    Owner(String login, String utl) {
        this.login = login;
        this.html_url = utl;
    }
}
