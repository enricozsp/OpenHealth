// OpenHealth - Frontend single-page wallet
const $ = (sel, root = document) => root.querySelector(sel);
const $$ = (sel, root = document) => Array.from(root.querySelectorAll(sel));

const toast = (msg, isError = false) => {
    const el = $('#toast');
    el.textContent = msg;
    el.classList.toggle('error', isError);
    el.classList.add('show');
    setTimeout(() => el.classList.remove('show'), 2400);
};

async function api(path, opts = {}) {
    const res = await fetch(path, {
        headers: { 'Content-Type': 'application/json' },
        ...opts
    });
    if (res.status === 401) {
        window.location.href = '/login.html';
        throw new Error('Sessão expirada');
    }
    if (!res.ok) {
        const text = await res.text().catch(() => '');
        throw new Error(`${res.status} ${text || res.statusText}`);
    }
    if (res.status === 204) return null;
    return res.json();
}

function formToObject(form) {
    const data = {};
    new FormData(form).forEach((value, key) => {
        data[key] = value === '' ? null : value;
    });
    return data;
}

function fillForm(form, data) {
    if (!data) return;
    Object.keys(data).forEach(k => {
        const field = form.elements.namedItem(k);
        if (field) field.value = data[k] ?? '';
    });
}

function clearForm(form) {
    form.reset();
    const idField = form.elements.namedItem('id');
    if (idField) idField.value = '';
    const submit = form.querySelector('button[type="submit"]');
    if (submit) submit.textContent = 'Adicionar';
}

// -------- Tabs --------
$$('.tab').forEach(btn => {
    btn.addEventListener('click', () => {
        $$('.tab').forEach(t => t.classList.remove('active'));
        $$('.panel').forEach(p => p.classList.remove('active'));
        btn.classList.add('active');
        $('#panel-' + btn.dataset.tab).classList.add('active');
    });
});

// -------- Perfil (singleton) --------
async function loadProfile() {
    const data = await api('/api/profile');
    fillForm($('#profileForm'), data);
    $('#ownerName').textContent = data.fullName || 'Meu Prontuário';
}

$('#profileForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const body = formToObject(e.target);
    try {
        const saved = await api('/api/profile', { method: 'PUT', body: JSON.stringify(body) });
        $('#ownerName').textContent = saved.fullName || 'Meu Prontuário';
        toast('Perfil salvo');
    } catch (err) { toast(err.message, true); }
});

// -------- Sharing (per doctor) --------
const SHARE_SECTIONS = [
    { key: 'shareProfile',       label: 'Perfil' },
    { key: 'shareAnamnese',      label: 'Anamnese' },
    { key: 'shareAllergies',     label: 'Alergias' },
    { key: 'shareVaccines',      label: 'Vacinas' },
    { key: 'shareSurgeries',     label: 'Cirurgias' },
    { key: 'shareConsultations', label: 'Consultas' },
    { key: 'shareExams',         label: 'Exames' }
];

async function loadSharing() {
    const [shares, doctors] = await Promise.all([
        api('/api/sharing'),
        api('/api/sharing/doctors')
    ]);
    renderDoctorSelector(doctors, shares);
    renderShares(shares);
}

function renderDoctorSelector(doctors, shares) {
    const sel = $('#doctorSelector');
    const sharedIds = new Set(shares.map(s => s.doctorId));
    const available = doctors.filter(d => !sharedIds.has(d.id));
    sel.innerHTML = '<option value="">— selecione um médico —</option>'
        + available.map(d => `
            <option value="${d.id}">${escapeHtml(d.fullName)}${d.crm ? ' · CRM ' + escapeHtml(d.crm) : ''}${d.email ? ' · ' + escapeHtml(d.email) : ''}</option>
        `).join('');
    if (!available.length) {
        sel.innerHTML += '<option disabled>Nenhum médico disponível</option>';
    }
}

function renderShares(shares) {
    const list = $('#sharesList');
    if (!shares.length) {
        list.innerHTML = '<div class="shares-empty">Você ainda não compartilhou seu prontuário com nenhum médico.</div>';
        return;
    }
    list.innerHTML = shares.map(s => `
        <div class="share-card" data-doctor-id="${s.doctorId}">
            <div class="share-card-head">
                <div>
                    <div class="share-doctor-name">${escapeHtml(s.doctorName || 'Médico')}</div>
                    <div class="share-doctor-meta">
                        ${s.doctorCrm ? 'CRM ' + escapeHtml(s.doctorCrm) : ''}
                        ${s.doctorEmail ? ' · ' + escapeHtml(s.doctorEmail) : ''}
                    </div>
                </div>
            </div>
            <div class="share-checks">
                ${SHARE_SECTIONS.map(sec => `
                    <label class="share-check">
                        <input type="checkbox" data-key="${sec.key}" ${s[sec.key] ? 'checked' : ''}>
                        <span>${escapeHtml(sec.label)}</span>
                    </label>
                `).join('')}
            </div>
            <div class="share-actions">
                <button type="button" class="ghost" data-revoke>Remover acesso</button>
                <button type="button" class="primary" data-save>Salvar alterações</button>
            </div>
        </div>
    `).join('');

    list.querySelectorAll('.share-card').forEach(card => {
        const doctorId = parseInt(card.dataset.doctorId, 10);
        card.querySelector('[data-save]').addEventListener('click', () => saveShare(card, doctorId));
        card.querySelector('[data-revoke]').addEventListener('click', () => revokeShare(doctorId));
    });
}

async function saveShare(card, doctorId) {
    const body = { doctorId };
    SHARE_SECTIONS.forEach(sec => {
        const cb = card.querySelector(`[data-key="${sec.key}"]`);
        body[sec.key] = !!(cb && cb.checked);
    });
    try {
        await api('/api/sharing', { method: 'POST', body: JSON.stringify(body) });
        toast('Compartilhamento atualizado');
    } catch (err) { toast(err.message, true); }
}

async function revokeShare(doctorId) {
    if (!confirm('Remover acesso deste médico ao seu prontuário?')) return;
    try {
        await api('/api/sharing/' + doctorId, { method: 'DELETE' });
        toast('Acesso removido');
        await loadSharing();
    } catch (err) { toast(err.message, true); }
}

$('#addDoctorBtn').addEventListener('click', async () => {
    const sel = $('#doctorSelector');
    const doctorId = parseInt(sel.value, 10);
    if (!doctorId) {
        toast('Selecione um médico para adicionar.', true);
        return;
    }
    const body = { doctorId };
    SHARE_SECTIONS.forEach(sec => { body[sec.key] = false; });
    try {
        await api('/api/sharing', { method: 'POST', body: JSON.stringify(body) });
        toast('Médico adicionado. Marque o que deseja compartilhar.');
        await loadSharing();
    } catch (err) { toast(err.message, true); }
});

// -------- Anamnese (singleton) --------
async function loadAnamnese() {
    const data = await api('/api/anamnese');
    fillForm($('#anamneseForm'), data);
}

$('#anamneseForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const body = formToObject(e.target);
    try {
        await api('/api/anamnese', { method: 'PUT', body: JSON.stringify(body) });
        toast('Anamnese salva');
    } catch (err) { toast(err.message, true); }
});

// -------- Generic list-form controller --------
function setupResource(opts) {
    const form = $('#' + opts.formId);
    const listEl = $('#' + opts.listId);
    const endpoint = form.dataset.endpoint;

    async function refresh() {
        try {
            const items = await api(endpoint);
            renderList(items);
        } catch (err) { toast(err.message, true); }
    }

    function renderList(items) {
        if (!items.length) {
            listEl.innerHTML = '<div class="empty">Nenhum registro ainda.</div>';
            return;
        }
        items.sort(opts.sort || ((a, b) => b.id - a.id));
        listEl.innerHTML = items.map(item => `
            <div class="record">
                <div class="record-header">
                    <div>
                        <div class="record-title">${escapeHtml(opts.title(item))}</div>
                        <div class="record-meta">${escapeHtml(opts.meta(item) || '')}</div>
                    </div>
                    <div class="record-actions">
                        <button class="edit" data-edit="${item.id}">Editar</button>
                        <button class="danger" data-del="${item.id}">Excluir</button>
                    </div>
                </div>
                ${opts.body ? `<div class="record-body">${opts.body(item)}</div>` : ''}
            </div>
        `).join('');

        listEl.querySelectorAll('[data-edit]').forEach(btn => {
            btn.addEventListener('click', () => {
                const item = items.find(i => String(i.id) === btn.dataset.edit);
                fillForm(form, item);
                form.querySelector('button[type="submit"]').textContent = 'Atualizar';
                form.scrollIntoView({ behavior: 'smooth', block: 'start' });
            });
        });

        listEl.querySelectorAll('[data-del]').forEach(btn => {
            btn.addEventListener('click', async () => {
                if (!confirm('Excluir este registro?')) return;
                try {
                    await api(`${endpoint}/${btn.dataset.del}`, { method: 'DELETE' });
                    toast('Removido');
                    refresh();
                } catch (err) { toast(err.message, true); }
            });
        });
    }

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const body = formToObject(e.target);
        const id = body.id;
        delete body.id;
        try {
            if (id) {
                await api(`${endpoint}/${id}`, { method: 'PUT', body: JSON.stringify(body) });
                toast('Atualizado');
            } else {
                await api(endpoint, { method: 'POST', body: JSON.stringify(body) });
                toast('Adicionado');
            }
            clearForm(form);
            refresh();
        } catch (err) { toast(err.message, true); }
    });

    form.querySelector('[data-clear]').addEventListener('click', () => clearForm(form));

    return refresh;
}

function escapeHtml(s) {
    if (s == null) return '';
    return String(s).replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
}

function row(label, val) {
    if (!val) return '';
    return `<span><b>${escapeHtml(label)}:</b> ${escapeHtml(val)}</span>`;
}

// -------- Resource wiring --------
const refreshAllergies = setupResource({
    formId: 'allergyForm',
    listId: 'allergyList',
    title: i => i.substance,
    meta: i => [i.severity, i.identifiedOn].filter(Boolean).join(' · '),
    body: i => row('Reação', i.reaction) + row('Observações', i.notes)
});

const refreshVaccines = setupResource({
    formId: 'vaccineForm',
    listId: 'vaccineList',
    title: i => i.name + (i.dose ? ` — ${i.dose}` : ''),
    meta: i => [i.appliedOn ? 'Aplicada em ' + i.appliedOn : null, i.nextDoseOn ? 'Próxima: ' + i.nextDoseOn : null].filter(Boolean).join(' · '),
    body: i => row('Fabricante', i.manufacturer) + row('Lote', i.batch) + row('Local', i.appliedAt) + row('Observações', i.notes),
    sort: (a, b) => (b.appliedOn || '').localeCompare(a.appliedOn || '')
});

const refreshSurgeries = setupResource({
    formId: 'surgeryForm',
    listId: 'surgeryList',
    title: i => i.procedure,
    meta: i => [i.performedOn, i.hospital].filter(Boolean).join(' · '),
    body: i => row('Cirurgião', i.surgeon) + row('Anestesia', i.anesthesia) + row('Observações', i.notes),
    sort: (a, b) => (b.performedOn || '').localeCompare(a.performedOn || '')
});

const refreshConsultations = setupResource({
    formId: 'consultationForm',
    listId: 'consultationList',
    title: i => (i.specialty || 'Consulta') + (i.professional ? ` — ${i.professional}` : ''),
    meta: i => [i.date, i.location].filter(Boolean).join(' · '),
    body: i => row('Motivo', i.reason) + row('Diagnóstico', i.diagnosis) + row('Prescrição', i.prescription) + row('Observações', i.notes),
    sort: (a, b) => (b.date || '').localeCompare(a.date || '')
});

const refreshExams = setupResource({
    formId: 'examForm',
    listId: 'examList',
    title: i => i.name,
    meta: i => [i.category, i.performedOn, i.laboratory].filter(Boolean).join(' · '),
    body: i => row('Solicitante', i.requestedBy) + row('Resultado', i.result) + row('Observações', i.notes),
    sort: (a, b) => (b.performedOn || '').localeCompare(a.performedOn || '')
});

// -------- Timeline --------
const TIMELINE_TYPES = {
    allergy:      { label: 'Alergia',   endpoint: '/api/allergies',     dateField: 'identifiedOn',
                    title: i => i.substance,
                    meta:  i => [i.severity, i.reaction].filter(Boolean).join(' · '),
                    body:  i => row('Observações', i.notes) },
    vaccine:      { label: 'Vacina',    endpoint: '/api/vaccines',      dateField: 'appliedOn',
                    title: i => i.name + (i.dose ? ` — ${i.dose}` : ''),
                    meta:  i => [i.appliedAt, i.manufacturer].filter(Boolean).join(' · '),
                    body:  i => row('Lote', i.batch) + row('Próxima dose', i.nextDoseOn) + row('Observações', i.notes) },
    surgery:      { label: 'Cirurgia',  endpoint: '/api/surgeries',     dateField: 'performedOn',
                    title: i => i.procedure,
                    meta:  i => [i.hospital, i.surgeon].filter(Boolean).join(' · '),
                    body:  i => row('Anestesia', i.anesthesia) + row('Observações', i.notes) },
    consultation: { label: 'Consulta',  endpoint: '/api/consultations', dateField: 'date',
                    title: i => (i.specialty || 'Consulta') + (i.professional ? ` — ${i.professional}` : ''),
                    meta:  i => [i.location, i.reason].filter(Boolean).join(' · '),
                    body:  i => row('Diagnóstico', i.diagnosis) + row('Prescrição', i.prescription) + row('Observações', i.notes) },
    exam:         { label: 'Exame',     endpoint: '/api/exams',         dateField: 'performedOn',
                    title: i => i.name,
                    meta:  i => [i.category, i.laboratory, i.requestedBy].filter(Boolean).join(' · '),
                    body:  i => row('Resultado', i.result) + row('Observações', i.notes) }
};

const MONTH_NAMES = ['Janeiro','Fevereiro','Março','Abril','Maio','Junho','Julho','Agosto','Setembro','Outubro','Novembro','Dezembro'];

let TIMELINE_DATA = [];
let TIMELINE_FILTER = 'all';

async function loadTimeline() {
    const lists = await Promise.all(
        Object.entries(TIMELINE_TYPES).map(async ([type, cfg]) => {
            try {
                const items = await api(cfg.endpoint);
                return items.map(item => ({
                    type,
                    typeLabel: cfg.label,
                    id: item.id,
                    date: item[cfg.dateField] || null,
                    title: cfg.title(item),
                    meta: cfg.meta(item),
                    body: cfg.body(item)
                }));
            } catch (e) { return []; }
        })
    );
    TIMELINE_DATA = lists.flat();
    renderTimeline();
}

function renderTimeline() {
    const container = $('#timeline');
    const undatedNote = $('#timelineUndated');
    const filtered = TIMELINE_FILTER === 'all'
        ? TIMELINE_DATA
        : TIMELINE_DATA.filter(r => r.type === TIMELINE_FILTER);

    const dated = filtered.filter(r => r.date).sort((a, b) => b.date.localeCompare(a.date));
    const undatedCount = filtered.filter(r => !r.date).length;

    if (!dated.length) {
        container.innerHTML = '<div class="empty">Nenhum registro datado para este filtro.</div>';
        undatedNote.textContent = undatedCount
            ? `${undatedCount} registro(s) sem data não exibido(s) na linha do tempo.`
            : '';
        return;
    }

    const groupedByYear = {};
    dated.forEach(r => {
        const [y, m] = r.date.split('-');
        if (!groupedByYear[y]) groupedByYear[y] = {};
        if (!groupedByYear[y][m]) groupedByYear[y][m] = [];
        groupedByYear[y][m].push(r);
    });

    const years = Object.keys(groupedByYear).sort((a, b) => b.localeCompare(a));
    container.innerHTML = years.map(y => {
        const months = Object.keys(groupedByYear[y]).sort((a, b) => b.localeCompare(a));
        const yearCount = months.reduce((s, m) => s + groupedByYear[y][m].length, 0);
        return `
            <div class="tl-year">
                <span class="tl-year-label">${escapeHtml(y)}</span>
                <span class="tl-year-count">${yearCount} registro${yearCount > 1 ? 's' : ''}</span>
            </div>
            ${months.map(m => `
                <div class="tl-month">${MONTH_NAMES[parseInt(m, 10) - 1] || m}</div>
                ${groupedByYear[y][m].map(r => renderTimelineItem(r)).join('')}
            `).join('')}
        `;
    }).join('');

    undatedNote.textContent = undatedCount
        ? `${undatedCount} registro(s) sem data não exibido(s) na linha do tempo.`
        : '';
}

function renderTimelineItem(r) {
    const day = r.date.split('-')[2];
    return `
        <div class="tl-item type-${r.type}">
            <div class="tl-day">${escapeHtml(day)}</div>
            <div class="tl-card type-${r.type}">
                <div class="tl-card-head">
                    <span class="tl-card-title">${escapeHtml(r.title || '(sem título)')}</span>
                    <span class="tl-type-badge type-${r.type}">${escapeHtml(r.typeLabel)}</span>
                </div>
                ${r.meta ? `<div class="tl-card-meta">${escapeHtml(r.meta)}</div>` : ''}
                ${r.body ? `<div class="tl-card-body">${r.body}</div>` : ''}
            </div>
        </div>
    `;
}

$('#timelineFilters').addEventListener('click', (e) => {
    const btn = e.target.closest('[data-filter]');
    if (!btn) return;
    TIMELINE_FILTER = btn.dataset.filter;
    $$('#timelineFilters .chip').forEach(c => c.classList.toggle('active', c === btn));
    renderTimeline();
});

$('#timelineRefresh').addEventListener('click', () => loadTimeline().catch(e => toast(e.message, true)));

// Auto-refresh timeline whenever its tab is opened
$$('.tab').forEach(btn => {
    if (btn.dataset.tab === 'timeline') {
        btn.addEventListener('click', () => loadTimeline().catch(e => toast(e.message, true)));
    }
});

// -------- Logout --------
$('#logoutBtn').addEventListener('click', async () => {
    try {
        await fetch('/api/auth/logout', { method: 'POST' });
    } catch (e) { /* ignore */ }
    window.location.href = '/login.html';
});

// -------- Bootstrap --------
(async function init() {
    try {
        const statusRes = await fetch('/api/auth/status');
        const status = await statusRes.json();
        if (!status.authenticated) {
            window.location.href = '/login.html';
            return;
        }
        await loadProfile();
        await loadAnamnese();
        await loadSharing();
        await Promise.all([
            refreshAllergies(),
            refreshVaccines(),
            refreshSurgeries(),
            refreshConsultations(),
            refreshExams()
        ]);
    } catch (err) {
        toast('Falha ao carregar dados: ' + err.message, true);
    }
})();