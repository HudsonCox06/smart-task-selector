function loadTasks() {
    fetch('http://localhost:8081/tasks')
    .then(response => response.json())
    .then(data => {
        const taskList = document.getElementById('task-list');
        taskList.innerHTML = '';
        data.forEach(task => {
            const listItem = document.createElement('li');
            listItem.textContent = `${task.title} | Priority: ${task.priority} | Category: ${task.category} | Status: ${task.status}`;
            taskList.appendChild(listItem);
        });
    })
}

function createTask() {
    const form = document.getElementById('create-task-form');

    fetch('http://localhost:8081/tasks', {
        method: 'POST',
        body: JSON.stringify({
            title: document.getElementById('title').value,
            priority: parseInt(document.getElementById('priority').value),
            category: document.getElementById('category').value
        }),
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(task => {
        loadTasks();
        form.reset();
    });
}

function claimBestTask() {
    const form = document.getElementById('claim-best-task-form');
    fetch('http://localhost:8081/tasks/claim-best', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            userId: document.getElementById('claim-user').value,
            category: document.getElementById('claim-category').value
        })
    })
    .then(response => response.json())
    .then(data => {
        document.getElementById('claim-result').textContent = `Claimed: ${data.task.title}`;
        loadTasks();
        form.reset();
    });
}

loadTasks();
document.getElementById('create-task-form').addEventListener('submit', function(event) {
    event.preventDefault();
    createTask();
});

document.getElementById('claim-best-task-form').addEventListener('submit', function(event) {
    event.preventDefault();
    claimBestTask();
});
document.getElementById('refresh-btn').addEventListener('click', loadTasks);