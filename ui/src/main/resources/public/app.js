const DEFAULT_HANDLER_CONFIG = {
    id: '',
    path: '',
    method: 'GET',
    type: 'STATIC',
    responseBody: '',
    responseCode: 200,
    groovyCode: '',
    routerPath: ''
};

// ── Elements ──────────────────────────────────────────────

const popupSettingsHandler = document.getElementById('settings-handler');
const textHandlerId = document.getElementById('h-id');
const textHandlerPath = document.getElementById('h-path');
const dropdownHandlerMethod = document.getElementById('h-method');
const dropdownHandlerType = document.getElementById('h-type');
const textHandlerResponseBody = document.getElementById('h-response-body');
const numberHandlerResponseCode = document.getElementById('h-response-code');
const textHandlerGroovyCode = document.getElementById('h-groovy-code');
const textHandlerRouterPath = document.getElementById('h-router-path');
const popupConfirmation = document.getElementById('settings-handler-confirmation');
const confirmationText = document.getElementById('confirmation-text');

const buttonHandlerDelete = document.getElementById('button-handler-delete');

const popupSettingsServer = document.getElementById('settings-server');
const numberServerMockPort = document.getElementById('s-mock-port');
const numberServerUiPort = document.getElementById('s-ui-port');

const fieldsStatic = document.querySelectorAll('.field-static');
const fieldsGroovy = document.querySelectorAll('.field-groovy');
const fieldsRouter = document.querySelectorAll('.field-router');

const tableBody = document.getElementById('handlers-table-body');

// ── State ─────────────────────────────────────────────────

let currentConfig = null;
let _confirmAction = null;

// ── Handlers ─────────────────────────────────────────────

function openHandlerSettings(config) {
    textHandlerId.value = config.id;
    textHandlerPath.value = config.path;
    dropdownHandlerMethod.value = config.method;
    dropdownHandlerType.value = config.type;
    textHandlerResponseBody.value = config.responseBody;
    numberHandlerResponseCode.value = config.responseCode;
    textHandlerGroovyCode.value = config.groovyCode;
    textHandlerRouterPath.value = config.routerPath;

    refreshHandlerTypeFields(config.type);
    buttonHandlerDelete.style.display = config.id ? '' : 'none';
    popupSettingsHandler.style.display = 'flex';
}

function closeHandlerSettings() {
    popupSettingsHandler.style.display = 'none';
}

function refreshHandlerTypeFields(type) {
    fieldsStatic.forEach(el => el.style.display = type === 'STATIC' ? '' : 'none');
    fieldsGroovy.forEach(el => el.style.display = type === 'GROOVY' ? '' : 'none');
    fieldsRouter.forEach(el => el.style.display = type === 'ROUTER' ? '' : 'none');
}

// ── Server settings ───────────────────────────────────────

function openServerSettings(config) {
    numberServerMockPort.value = config.mockPort;
    numberServerUiPort.value = config.uiPort;

    popupSettingsServer.style.display = 'flex';
}

function closeServerSettings() {
    popupSettingsServer.style.display = 'none';
}

function openSaveServerConfirmation() {
    const mockPort = numberServerMockPort.value;
    const uiPort = numberServerUiPort.value;
    openConfirmation(`Update ports — mock: ${mockPort}, ui: ${uiPort}?`, saveServerSettings);
}

async function saveServerSettings() {
    await fetch('/api/conf', {
        method: 'PATCH',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({
            mockPort: parseInt(numberServerMockPort.value),
            uiPort: parseInt(numberServerUiPort.value)
        })
    });
    closeServerSettings();
}

// ── Save / Delete ─────────────────────────────────────────

function openSaveConfirmation() {
    const method = dropdownHandlerMethod.value;
    const path = textHandlerPath.value;
    const id = textHandlerId.value;
    const label = id ? `Update ${method} ${path}?` : `Create ${method} ${path}?`;
    openConfirmation(label, saveHandler);
}

async function saveHandler() {
    const id = textHandlerId.value;
    const body = JSON.stringify({
        id,
        path: textHandlerPath.value,
        method: dropdownHandlerMethod.value,
        type: dropdownHandlerType.value,
        responseBody: textHandlerResponseBody.value,
        responseCode: parseInt(numberHandlerResponseCode.value),
        groovyCode: textHandlerGroovyCode.value,
        routerPath: textHandlerRouterPath.value
    });

    await fetch('/api/handler', {
        method: id ? 'PATCH' : 'PUT',
        headers: {'Content-Type': 'application/json'},
        body
    });
}

function openDeleteConfirmation() {
    const method = dropdownHandlerMethod.value;
    const path = textHandlerPath.value;
    openConfirmation(`Delete ${method} ${path}?`, deleteHandler);
}

async function deleteHandler() {
    const id = textHandlerId.value;
    await fetch(`/api/handler/${id}`, {method: 'DELETE'});
}

function openConfirmation(text, action) {
    confirmationText.textContent = text;
    _confirmAction = action;
    popupConfirmation.style.display = 'flex';
}

function closeConfirmation() {
    popupConfirmation.style.display = 'none';
    _confirmAction = null;
}

async function runConfirmAction() {
    if (_confirmAction) await _confirmAction();
    closeConfirmation();
    closeHandlerSettings();
    await loadConfig();
}

// ── Config / Table ────────────────────────────────────────

async function loadConfig() {
    const response = await fetch('/api/conf');
    currentConfig = await response.json();

    tableBody.innerHTML = '';
    (currentConfig.handlers || [])
        .sort((a, b) => a.path.localeCompare(b.path) || a.method.localeCompare(b.method) || a.type.localeCompare(b.type))
        .forEach(handler => {
            const row = document.createElement('tr');
            row.innerHTML = `
            <td><span class="badge badge-${handler.type.toLowerCase()}">${handler.type}</span></td>
            <td>${handler.method}</td>
            <td>${handler.path}</td>
            <td><button onclick="openHandlerSettings(currentConfig.handlers.find(h => h.id === '${handler.id}'))">Edit</button></td>
        `;
            tableBody.appendChild(row);
        });
}

// ── Listeners ────────────────────────────────────────────

document.getElementById('button-add-handler').addEventListener('click', () => {
    openHandlerSettings(DEFAULT_HANDLER_CONFIG);
});
document.getElementById('button-handler-cancel').addEventListener('click', closeHandlerSettings);
dropdownHandlerType.addEventListener('change', () => refreshHandlerTypeFields(dropdownHandlerType.value));

document.getElementById('button-handler-save').addEventListener('click', openSaveConfirmation);
document.getElementById('button-handler-delete').addEventListener('click', openDeleteConfirmation);
document.getElementById('button-confirmation-confirm').addEventListener('click', runConfirmAction);
document.getElementById('button-confirmation-cancel').addEventListener('click', closeConfirmation);

document.getElementById('button-settings').addEventListener('click', () => openServerSettings(currentConfig));
document.getElementById('button-server-cancel').addEventListener('click', closeServerSettings);
document.getElementById('button-server-save').addEventListener('click', openSaveServerConfirmation);

loadConfig();
