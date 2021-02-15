class Config {
    private config: ConfigSchema;

    constructor() {
        this.config = {
            lastServerConnection: null,
            lastPlayerUuid: null,
            fetchPlayerList: true
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

    get serverConnection(): ServerConnection | null {
        return this.config.lastServerConnection || null;
    }

    set serverConnection(data: ServerConnection | null) {
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
    lastServerConnection: ServerConnection | null,
    lastPlayerUuid: string | null,
    fetchPlayerList: boolean
}

interface ServerConnection {
    address: string,
    port: number,
    key: string
}

export default new Config;