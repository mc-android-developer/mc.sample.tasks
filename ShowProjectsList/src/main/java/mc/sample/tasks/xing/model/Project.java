package mc.sample.tasks.xing.model;

public class Project {
    private final int id;
    private final String name;
    private final String description;
    private final Owner owner;
    private final boolean fork;

    public Project(int id, String name, String description, String url, String login, boolean fork) {
        this.id = id;
        this.name = name;
        this.description = description;
        owner = new Owner(login, url);
        this.fork = fork;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isFork() {
        return fork;
    }

    public String getLogin() {
        return owner.login;
    }

    public String getUrl() {
        return owner.html_url;
    }
}
