// OpenHealth - Doctor dashboard
const $ = (sel, root = document) => root.querySelector(sel);
const $$ = (sel, root = document) => Array.from(root.querySelectorAll(sel));

let currentDoctorId = null;
let currentPatientId = null;
let editingRecadoId = null;
let recadosCache = [];

const toast = (msg, isError = false) => {
    const el = $('#toast');
    el.textContent = msg;
    el.classList.toggle('error', isError);
    el.classList.add('show');
    setTimeout(() => el.classList.remove('show'), 2400);
};

function escapeHtml(s) {
    if (s == null) return '';
    return String(s).replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
}

function formatWhen(iso) {
    if (!iso) return '';
    try {
        const d = new Date(iso);
        return d.toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' });
    } catch (e) { return iso; }
}

async function api(path, opts = {}) {
    const res = await fetch(path, { headers: { 'Content-Type': 'application/json' }, ...opts });
    if (res.status === 401) {
        window.location.href = '/login-medico.html';
        throw new Error('Sessão expirada');
    }
    if (!res.ok) {
        const text = await res.text().catch(() => '');
        throw new Error(`${res.status} ${text || res.statusText}`);
    }
    if (res.status === 204) return null;
    return res.json();
}

const SECTIONS = [
    { key: 'profile',       label: 'Perfil' },
    { key: 'anamnese',      label: 'Anamnese' },
    { key: 'allergies',     label: 'Alergias' },
    { key: 'vaccines',      label: 'Vacinas' },
    { key: 'surgeries',     label: 'Cirurgias' },
    { key: 'consultations', label: 'Consultas' },
    { key: 'exams',         label: 'Exames' }
];

function kvRow(label, val) {
    if (val == null || val === '') return '';
    return `<div class="kv"><b>${escapeHtml(label)}:</b> ${escapeHtml(val)}</div>`;
}

function renderProfile(p) {
    if (!p) return '<div class="section-empty">Sem dados de perfil.</div>';
    return `<div class="kv-grid">
        ${kvRow('Nome', p.fullName)}
        ${kvRow('Email', p.email)}
        ${kvRow('Data de nascimento', p.birthDate)}
        ${kvRow('Gênero', p.gender)}
        ${kvRow('Tipo sanguíneo', p.bloodType)}
        ${kvRow('Documento', p.document)}
        ${kvRow('Telefone', p.phone)}
        ${kvRow('Contato de emergência', p.emergencyContact)}
        ${kvRow('Observações', p.notes)}
    </div>`;
}

function renderAnamnese(a) {
    if (!a) return '<div class="section-empty">Sem anamnese registrada.</div>';
    return `<div class="kv-grid">
        ${kvRow('Queixa principal', a.mainComplaint)}
        ${kvRow('História da doença atual', a.currentDiseaseHistory)}
        ${kvRow('Antecedentes pessoais', a.personalHistory)}
        ${kvRow('Antecedentes familiares', a.familyHistory)}
        ${kvRow('Hábitos de vida', a.lifeHabits)}
        ${kvRow('Medicações em uso', a.medications)}
        ${kvRow('Outras informações', a.otherInfo)}
    </div>`;
}

function renderRecord(title, meta, body) {
    return `<div class="record read-only">
        <div class="record-header">
            <div>
                <div class="record-title">${escapeHtml(title)}</div>
                ${meta ? `<div class="record-meta">${escapeHtml(meta)}</div>` : ''}
            </div>
        </div>
        ${body ? `<div class="record-body">${body}</div>` : ''}
    </div>`;
}

function renderList(items, fn) {
    if (!items || !items.length) return '<div class="section-empty">Nenhum registro.</div>';
    return `<div class="list">${items.map(fn).join('')}</div>`;
}

function row(label, val) {
    if (!val) return '';
    return `<span><b>${escapeHtml(label)}:</b> ${escapeHtml(val)}</span>`;
}

function renderAllergies(items) {
    return renderList(items, i => renderRecord(
        i.substance,
        [i.severity, i.identifiedOn].filter(Boolean).join(' · '),
        row('Reação', i.reaction) + row('Observações', i.notes)
    ));
}

function renderVaccines(items) {
    return renderList(items, i => renderRecord(
        i.name + (i.dose ? ` — ${i.dose}` : ''),
        [i.appliedOn ? 'Aplicada em ' + i.appliedOn : null, i.nextDoseOn ? 'Próxima: ' + i.nextDoseOn : null].filter(Boolean).join(' · '),
        row('Fabricante', i.manufacturer) + row('Lote', i.batch) + row('Local', i.appliedAt) + row('Observações', i.notes)
    ));
}

function renderSurgeries(items) {
    return renderList(items, i => renderRecord(
        i.procedure,
        [i.performedOn, i.hospital].filter(Boolean).join(' · '),
        row('Cirurgião', i.surgeon) + row('Anestesia', i.anesthesia) + row('Observações', i.notes)
    ));
}

function renderConsultations(items) {
    return renderList(items, i => renderRecord(
        (i.specialty || 'Consulta') + (i.professional ? ` — ${i.professional}` : ''),
        [i.date, i.location].filter(Boolean).join(' · '),
        row('Motivo', i.reason) + row('Diagnóstico', i.diagnosis) + row('Prescrição', i.prescription) + row('Observações', i.notes)
    ));
}

function renderExams(items) {
    return renderList(items, i => renderRecord(
        i.name,
        [i.category, i.performedOn, i.laboratory].filter(Boolean).join(' · '),
        row('Solicitante', i.requestedBy) + row('Resultado', i.result) + row('Observações', i.notes)
    ));
}

const RENDERERS = {
    profile: renderProfile,
    anamnese: renderAnamnese,
    allergies: renderAllergies,
    vaccines: renderVaccines,
    surgeries: renderSurgeries,
    consultations: renderConsultations,
    exams: renderExams
};

function renderProntuario(data) {
    const panel = $('#patientPanel');
    const header = $('#patientHeader');
    const sub = $('#patientSubheader');
    const badges = $('#sharingBadges');
    const content = $('#prontuarioContent');

    panel.style.display = 'block';
    header.textContent = 'Prontuário · ' + (data.patient.fullName || 'Paciente');
    sub.textContent = data.patient.email || '';

    badges.innerHTML = SECTIONS.map(s => {
        const on = data.sharing && data.sharing[s.key];
        return `<span class="badge ${on ? 'on' : 'off'}">${escapeHtml(s.label)}</span>`;
    }).join('');

    const sharedKeys = SECTIONS.filter(s => data.sharing && data.sharing[s.key]);
    if (!sharedKeys.length) {
        content.innerHTML = '<div class="section-empty">O paciente ainda não marcou nenhuma seção para você ver.</div>';
        panel.scrollIntoView({ behavior: 'smooth', block: 'start' });
        return;
    }
    content.innerHTML = sharedKeys.map(s =>
        `<h3>${escapeHtml(s.label)}</h3>${RENDERERS[s.key](data[s.key])}`
    ).join('');
    panel.scrollIntoView({ behavior: 'smooth', block: 'start' });
}

function renderRequests(shares) {
    const list = $('#requestsList');
    if (!shares.length) {
        list.innerHTML = '<div class="requests-empty">Nenhum paciente compartilhou dados com você ainda.</div>';
        $('#patientPanel').style.display = 'none';
        return;
    }
    list.innerHTML = shares.map(s => {
        const sharedSections = SECTIONS.filter(sec => s.sharing && s.sharing[sec.key]).map(sec => sec.label);
        const summary = sharedSections.length
            ? `<b>${sharedSections.length}</b> seção(ões): ${sharedSections.map(escapeHtml).join(', ')}`
            : '<i>Nenhuma seção marcada ainda — aguarde o paciente liberar</i>';
        return `
            <div class="request-card" data-patient-id="${s.patientId}">
                <div class="request-head">
                    <span class="request-name">${escapeHtml(s.patientName || 'Paciente')}</span>
                    <span class="request-when">${escapeHtml(formatWhen(s.updatedAt || s.createdAt))}</span>
                </div>
                <div class="request-email">${escapeHtml(s.patientEmail || '')}</div>
                <div class="request-summary">${summary}</div>
            </div>
        `;
    }).join('');

    list.querySelectorAll('.request-card').forEach(card => {
        card.addEventListener('click', () => openProntuario(parseInt(card.dataset.patientId, 10), card));
    });
}

async function openProntuario(patientId, cardEl) {
    $$('.request-card').forEach(c => c.classList.toggle('active', c === cardEl));
    // Trocar de paciente cancela qualquer edição pendente para evitar
    // que um PUT seja disparado contra um recado de outro paciente.
    cancelEditRecado();
    try {
        const data = await api('/api/doctor/patients/' + patientId + '/prontuario');
        if (!data.found) {
            toast('Prontuário não encontrado.', true);
            return;
        }
        currentPatientId = patientId;
        renderProntuario(data);
        await loadRecados();
    } catch (err) {
        toast(err.message, true);
    }
}

function statusBadge(item) {
    if (item.status === 'LIDO') {
        const when = item.dataLeitura ? ' em ' + formatWhen(item.dataLeitura) : '';
        return `<span class="recado-status lido">Lido${escapeHtml(when)}</span>`;
    }
    return '<span class="recado-status nao-lido">Não lido</span>';
}

function renderRecados(items) {
    const list = $('#recadosList');
    recadosCache = items || [];
    if (!items || !items.length) {
        list.innerHTML = '<div class="section-empty">Nenhum recado enviado para este paciente ainda.</div>';
        return;
    }
    list.innerHTML = items.map(r => `
        <div class="recado-card ${r.status === 'LIDO' ? 'lido' : 'nao-lido'}" data-recado-id="${r.id}">
            <div class="recado-head">
                <span class="recado-title">${escapeHtml(r.titulo)}</span>
                ${statusBadge(r)}
            </div>
            <div class="recado-meta">Enviado em ${escapeHtml(formatWhen(r.dataCriacao))}</div>
            <div class="recado-body">${escapeHtml(r.mensagem)}</div>
            <div class="recado-actions">
                <button type="button" class="ghost" data-edit-recado="${r.id}">Editar</button>
                <button type="button" class="danger" data-del-recado="${r.id}">Excluir</button>
            </div>
        </div>
    `).join('');

    list.querySelectorAll('[data-edit-recado]').forEach(btn => {
        btn.addEventListener('click', () => beginEditRecado(parseInt(btn.dataset.editRecado, 10)));
    });
    list.querySelectorAll('[data-del-recado]').forEach(btn => {
        btn.addEventListener('click', () => deleteRecado(parseInt(btn.dataset.delRecado, 10)));
    });
}

function beginEditRecado(recadoId) {
    const r = recadosCache.find(x => x.id === recadoId);
    if (!r) return;
    editingRecadoId = recadoId;
    const form = $('#recadoForm');
    form.elements.titulo.value = r.titulo || '';
    form.elements.mensagem.value = r.mensagem || '';
    $('#recadoSubmitBtn').textContent = 'Salvar alterações';
    $('#recadoEditBanner').style.display = 'flex';
    $('#recadoEditBannerText').textContent = 'Editando: ' + r.titulo;
    form.scrollIntoView({ behavior: 'smooth', block: 'start' });
    form.elements.titulo.focus();
}

function cancelEditRecado() {
    editingRecadoId = null;
    resetRecadoForm();
}

async function deleteRecado(recadoId) {
    if (!confirm('Excluir este recado? Esta ação não pode ser desfeita.')) return;
    try {
        await api('/api/recados-medicos/' + recadoId, { method: 'DELETE' });
        toast('Recado excluído.');
        if (editingRecadoId === recadoId) cancelEditRecado();
        await loadRecados();
    } catch (err) {
        toast('Falha ao excluir: ' + err.message, true);
    }
}

async function loadRecados() {
    if (!currentDoctorId || !currentPatientId) return;
    const list = $('#recadosList');
    list.innerHTML = '<div class="section-empty">Carregando...</div>';
    try {
        const items = await api(`/api/recados-medicos/medico/${currentDoctorId}/paciente/${currentPatientId}`);
        renderRecados(items);
    } catch (err) {
        list.innerHTML = `<div class="section-empty error">Falha ao carregar recados: ${escapeHtml(err.message)}</div>`;
    }
}

function resetRecadoForm() {
    const form = $('#recadoForm');
    if (form) form.reset();
    editingRecadoId = null;
    const submit = $('#recadoSubmitBtn');
    if (submit) submit.textContent = 'Enviar recado';
    const banner = $('#recadoEditBanner');
    if (banner) banner.style.display = 'none';
}

async function submitRecado(ev) {
    ev.preventDefault();
    if (!currentDoctorId) {
        toast('Sessão do médico não identificada.', true);
        return;
    }
    if (!currentPatientId) {
        toast('Abra o prontuário de um paciente primeiro.', true);
        return;
    }
    const form = ev.currentTarget;
    const data = new FormData(form);
    const titulo = (data.get('titulo') || '').toString().trim();
    const mensagem = (data.get('mensagem') || '').toString().trim();
    if (!titulo || !mensagem) {
        toast('Preencha título e mensagem.', true);
        return;
    }
    const btn = $('#recadoSubmitBtn');
    btn.disabled = true;
    try {
        if (editingRecadoId) {
            await api('/api/recados-medicos/' + editingRecadoId, {
                method: 'PUT',
                body: JSON.stringify({ titulo, mensagem })
            });
            toast('Recado atualizado.');
        } else {
            await api('/api/recados-medicos', {
                method: 'POST',
                body: JSON.stringify({
                    medicoId: currentDoctorId,
                    pacienteId: currentPatientId,
                    titulo,
                    mensagem
                })
            });
            toast('Recado enviado.');
        }
        resetRecadoForm();
        await loadRecados();
    } catch (err) {
        toast('Falha: ' + err.message, true);
    } finally {
        btn.disabled = false;
    }
}

async function loadRequests() {
    try {
        const shares = await api('/api/doctor/shares');
        renderRequests(shares);
    } catch (err) {
        toast('Falha ao carregar requisições: ' + err.message, true);
    }
}

function on(sel, evt, fn) {
    const el = $(sel);
    if (el) el.addEventListener(evt, fn);
    else console.warn('Elemento ausente para listener:', sel);
}

on('#refreshBtn', 'click', loadRequests);
on('#recadoForm', 'submit', submitRecado);
on('#recadoClearBtn', 'click', resetRecadoForm);
on('#recadosRefreshBtn', 'click', loadRecados);
on('#recadoCancelEditBtn', 'click', cancelEditRecado);

on('#logoutBtn', 'click', async () => {
    try { await fetch('/api/doctor-auth/logout', { method: 'POST' }); } catch (e) { /* ignore */ }
    window.location.href = '/login-medico.html';
});

(async function init() {
    try {
        const statusRes = await fetch('/api/doctor-auth/status');
        const status = await statusRes.json();
        if (!status.authenticated) {
            window.location.href = '/login-medico.html';
            return;
        }
        const me = await fetch('/api/doctor-auth/me').then(r => r.ok ? r.json() : null);
        if (me) {
            currentDoctorId = me.id;
            $('#doctorName').textContent = (me.fullName || 'Médico') + (me.crm ? ' · CRM ' + me.crm : '');
        }
        await loadRequests();
    } catch (err) {
        toast('Falha ao carregar: ' + err.message, true);
    }
})();