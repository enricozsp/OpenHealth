// OpenHealth - Doctor dashboard
const $ = (sel, root = document) => root.querySelector(sel);
const $$ = (sel, root = document) => Array.from(root.querySelectorAll(sel));

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
    try {
        const data = await api('/api/doctor/patients/' + patientId + '/prontuario');
        if (!data.found) {
            toast('Prontuário não encontrado.', true);
            return;
        }
        renderProntuario(data);
    } catch (err) {
        toast(err.message, true);
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

$('#refreshBtn').addEventListener('click', loadRequests);

$('#logoutBtn').addEventListener('click', async () => {
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
            $('#doctorName').textContent = (me.fullName || 'Médico') + (me.crm ? ' · CRM ' + me.crm : '');
        }
        await loadRequests();
    } catch (err) {
        toast('Falha ao carregar: ' + err.message, true);
    }
})();