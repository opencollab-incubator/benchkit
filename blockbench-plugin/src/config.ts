class Config {
    private config: ConfigSchema;

    constructor() {
        this.config = {
            lastServerConnection: null,
            lastPlayerUuid: null,
            fetchPlayerList: true,
            strategies: {
                github: {
                    accessToken: null
                }
                // TODO: git
                // TODO: google drive
                // TODO: local
            }
        }
        this.load();
    }

    private update() {
        localStorage.setItem("benchkit-config", JSON.stringify(this.config));
        console.log("Benchkit> Updated config");
    }

    private load() {
        let savedConfig = localStorage.getItem("benchkit-config");

        if (savedConfig !== null) {
            this.config = <ConfigSchema> JSON.parse(savedConfig);
        }
    }

    get serverConnection(): ServerConnectionSchema | null {
        return this.config.lastServerConnection || null;
    }

    set serverConnection(data: ServerConnectionSchema | null) {
        this.config.lastServerConnection = data;
        this.update();
        
    }

    get lastPlayerUuid(): string | null {
        return this.config.lastPlayerUuid || null;
    }

    set lastPlayerUuid(uuid: string | null) {
        this.config.lastPlayerUuid = uuid;
        this.update();
    }

    get fetchPlayerList(): boolean {
        return this.config.fetchPlayerList || true;
    }

    set fetchPlayerList(value: boolean) {
        this.config.fetchPlayerList = value;
        this.update();
    }
}

interface ConfigSchema {
    lastServerConnection: ServerConnectionSchema | null,
    lastPlayerUuid: string | null,
    fetchPlayerList: boolean,
    strategies: StrategySchema
}

interface ServerConnectionSchema {
    address: string,
    port: number,
    key: string
}

interface StrategySchema {
    github: GithubStrategySchema
}

interface GithubStrategySchema {
    accessToken: string | null
}

export default new Config;