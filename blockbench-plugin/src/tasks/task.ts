interface Task {
    execute(data: object): void;
    
    handle(data: object): void;
}