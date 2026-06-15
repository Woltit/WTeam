import { useEffect, useState, type FormEvent } from 'react';
import usersApi from '../api/users';
import itemsApi from '../api/items';
import profileApi from '../api/profile';
import categoriesApi from '../api/categories';
import bookingsApi, { type BookingResponse, type BookingStatus } from '../api/bookings';
import adminApi, { type AdminStatsResponse } from '../api/admin';
import type { UserResponse, Role, VerificationStatus, PendingProfileResponse } from '../types/user';
import type { ItemResponse } from '../types/item';
import { useLanguage } from '../contexts/LanguageContext';
import type { CategoryResponse, CategoryRequest } from '../types/category';

type Tab = 'stats' | 'users' | 'items' | 'verifications' | 'categories' | 'bookings';

const BOOKING_STATUSES: BookingStatus[] = [
    'PENDING', 'APPROVED', 'REJECTED', 'PAID', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'DISPUTE',
];



// ── Stats Tab ────────────────────────────────────────────
const StatsTab = () => {
    const { t } = useLanguage();
    const [stats, setStats] = useState<AdminStatsResponse | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        adminApi.getStats()
            .then(setStats)
            .catch(() => setError('Не вдалося завантажити статистику.'))
            .finally(() => setLoading(false));
    }, []);

    if (loading) return <div className="spinner" />;
    if (error) return <div className="alert alert-error">{error}</div>;
    if (!stats) return null;

    const maxCount = Math.max(...stats.topCategories.map(c => c.itemCount), 1);

    return (
        <div className="admin-tab">
            <div className="stats-grid">
                <div className="stat-card">
                    <span className="stat-card-icon">👤</span>
                    <span className="stat-card-value">{stats.totalUsers}</span>
                    <span className="stat-card-label">{t('admin.statsUsers')}</span>
                </div>
                <div className="stat-card">
                    <span className="stat-card-icon">📦</span>
                    <span className="stat-card-value">{stats.totalItems}</span>
                    <span className="stat-card-label">{t('admin.statsItems')}</span>
                </div>
                <div className="stat-card">
                    <span className="stat-card-icon">🔄</span>
                    <span className="stat-card-value">{stats.activeBookings}</span>
                    <span className="stat-card-label">{t('admin.statsActiveRents')}</span>
                </div>
                <div className="stat-card">
                    <span className="stat-card-icon">✅</span>
                    <span className="stat-card-value">{stats.completedBookings}</span>
                    <span className="stat-card-label">{t('admin.statsCompletedRents')}</span>
                </div>
            </div>

            <div className="stats-section">
                <h2 className="section-heading">{t('admin.statsPopCategories')}</h2>
                {stats.topCategories.length === 0
                    ? <p style={{ color: 'var(--text)' }}>{t('admin.statsNoData')}.</p>
                    : stats.topCategories.map(c => (
                        <div key={c.categoryName} className="stats-bar-row">
                            <span className="stats-bar-label">{c.categoryName}</span>
                            <div className="stats-bar-track">
                                <div
                                    className="stats-bar-fill"
                                    style={{ width: `${(c.itemCount / maxCount) * 100}%` }}
                                />
                            </div>
                            <span className="stats-bar-count">{c.itemCount}</span>
                        </div>
                    ))}
            </div>
        </div>
    );
};

// ── Users Tab ────────────────────────────────────────────
const UsersTab = () => {
    const { t } = useLanguage();
    const [users, setUsers] = useState<UserResponse[]>([]);
    const [total, setTotal] = useState(0);
    const [page, setPage] = useState(0);
    const [search, setSearch] = useState('');
    const [loading, setLoading] = useState(true);
    const [msg, setMsg] = useState('');

    const load = (p = 0) => {
        setLoading(true);
        usersApi.getAllUsers(p, 15)
            .then(data => { setUsers(data.content); setTotal(data.totalPages); setPage(p); })
            .catch(() => setMsg('Не вдалося завантажити користувачів.'))
            .finally(() => setLoading(false));
    };

    useEffect(() => { load(); }, []);

    const handleSearch = async (e: FormEvent) => {
        e.preventDefault();
        if (!search.trim()) { load(); return; }
        setLoading(true);
        try {
            const user = await usersApi.searchUserByEmail(search.trim());
            setUsers([user]);
            setTotal(1);
        } catch { setMsg('Користувача не знайдено.'); setUsers([]); }
        finally { setLoading(false); }
    };

    const doAction = async (action: () => Promise<void>) => {
        try { await action(); setMsg(''); load(page); } catch { setMsg('Дію не виконано.'); }
    };

    return (
        <div className="admin-tab">
            <form className="admin-search" onSubmit={handleSearch}>
                <input className="form-input" placeholder={t('admin.usersSearchPlaceholder')} value={search} onChange={e => setSearch(e.target.value)} />
                <button className="btn btn-outline btn-sm" type="submit">{t('admin.usersSearchBtn')}</button>
                <button className="btn btn-outline btn-sm" type="button" onClick={() => { setSearch(''); load(); }}>{t('admin.usersResetBtn')}</button>
            </form>

            {msg && <div className="alert alert-error">{msg}</div>}

            {loading ? <div className="spinner" /> : (
                <div className="admin-table-wrap">
                    <table className="admin-table">
                        <thead>
                            <tr>
                                <th>{t('admin.usersColId')}</th><th>{t('admin.usersColEmail')}</th><th>{t('admin.usersColName')}</th><th>{t('admin.usersColRole')}</th>
                                <th>{t('admin.usersColAccount')}</th><th>{t('admin.usersColVerification')}</th><th>{t('admin.usersColActions')}</th>
                            </tr>
                        </thead>
                        <tbody>
                            {users.map(u => (
                                <tr key={u.id}>
                                    <td>{u.id}</td>
                                    <td>{u.email}</td>
                                    <td>{u.profile?.firstName} {u.profile?.lastName}</td>
                                    <td>
                                        <select className="role-select"
                                            value={u.role}
                                            onChange={e => doAction(() => usersApi.updateRole(u.id, e.target.value as Role))}>
                                            {(['USER', 'MODER', 'ADMIN'] as Role[]).map(r => <option key={r} value={r}>{r}</option>)}
                                        </select>
                                    </td>
                                    <td>
                                        <span className={`badge ${u.isActive ? 'badge-success' : 'badge-error'}`}>
                                            {u.isActive ? t('admin.usersActive') : t('admin.usersBlocked')}
                                        </span>
                                        {!u.isActive && u.blockReason && (
                                            <div className="admin-hint" title={u.blockReason}>{t('admin.usersBlockReason').replace('{reason}', u.blockReason)}</div>
                                        )}
                                    </td>
                                    <td>
                                        <span className={`badge ${u.profile?.verificationStatus === 'VERIFIED' ? 'badge-success' : 'badge-neutral'}`}>
                                            {t('verificationStatus.' + (u.profile?.verificationStatus ?? 'UNVERIFIED'))}
                                        </span>
                                    </td>
                                    <td className="action-cell">
                                        {!u.isActive && (
                                            <button className="btn btn-outline btn-xs"
                                                onClick={() => doAction(() => usersApi.activateUser(u.id))}>
                                                {t('admin.usersActivateBtn')}
                                            </button>
                                        )}
                                        {u.isActive && (
                                            <button className="btn btn-warning btn-xs"
                                                onClick={() => {
                                                    const reason = prompt(t('admin.usersPromptBlockReason'));
                                                    if (reason) doAction(() => usersApi.blockUser(u.id, { reason }));
                                                }}>
                                                {t('admin.usersBlockBtn')}
                                            </button>
                                        )}
                                        <button className="btn btn-danger btn-xs"
                                            onClick={() => {
                                                if (confirm(t('admin.usersConfirmDelete').replace('{email}', u.email)))
                                                    doAction(() => usersApi.deleteUser(u.id));
                                            }}>
                                            {t('admin.usersDeleteBtn')}
                                        </button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}
            {total > 1 && (
                <div className="pagination">
                    <button className="btn btn-outline btn-sm" disabled={page === 0} onClick={() => load(page - 1)}>← Назад</button>
                    <span className="pagination-info">Сторінка {page + 1} з {total}</span>
                    <button className="btn btn-outline btn-sm" disabled={page >= total - 1} onClick={() => load(page + 1)}>Далі →</button>
                </div>
            )}
        </div>
    );
};

// ── Items Tab ────────────────────────────────────────────
const ItemsTab = () => {
    const { t } = useLanguage();
    const [items, setItems] = useState<ItemResponse[]>([]);
    const [total, setTotal] = useState(0);
    const [page, setPage] = useState(0);
    const [loading, setLoading] = useState(true);
    const [msg, setMsg] = useState('');

    const load = (p = 0) => {
        setLoading(true);
        itemsApi.getAllItems(p, 15)
            .then(data => { setItems(data.content); setTotal(data.totalPages); setPage(p); })
            .catch(() => setMsg('Не вдалося завантажити оголошення.'))
            .finally(() => setLoading(false));
    };

    useEffect(() => { load(); }, []);

    const handleDelete = async (id: number) => {
        if (!confirm(t('admin.itemsConfirmDelete'))) return;
        try { await itemsApi.deleteItem(id); setMsg(''); load(page); } catch { setMsg('Не вдалося видалити.'); }
    };

    const toggleVerified = async (item: ItemResponse) => {
        try {
            await itemsApi.setItemVerified(item.id, !item.isVerified);
            setMsg('');
            load(page);
        } catch { setMsg('Не вдалося змінити статус верифікації.'); }
    };

    return (
        <div className="admin-tab">
            {msg && <div className="alert alert-error">{msg}</div>}
            {loading ? <div className="spinner" /> : (
                <div className="admin-table-wrap">
                    <table className="admin-table">
                        <thead>
                            <tr>
                                <th>{t('admin.itemsColId')}</th><th>{t('admin.itemsColTitle')}</th><th>{t('admin.itemsColOwner')}</th><th>{t('admin.itemsColCity')}</th>
                                <th>{t('admin.itemsColStatus')}</th><th>{t('admin.itemsColVerification')}</th><th>{t('admin.itemsColPrice')}</th><th>{t('admin.usersColActions')}</th>
                            </tr>
                        </thead>
                        <tbody>
                            {items.map(item => (
                                <tr key={item.id}>
                                    <td>{item.id}</td>
                                    <td>{item.title}</td>
                                    <td>{item.ownerProfile.firstName} {item.ownerProfile.lastName}</td>
                                    <td>{item.city}</td>
                                    <td><span className="badge badge-neutral">{item.status}</span></td>
                                    <td>
                                        <span className={`badge ${item.isVerified ? 'badge-success' : 'badge-warning'}`}>
                                            {item.isVerified ? t('admin.itemsYes') : t('admin.itemsNo')}
                                        </span>
                                    </td>
                                    <td>₴{item.pricePerDay}</td>
                                    <td className="action-cell">
                                        <button
                                            className={`btn btn-xs ${item.isVerified ? 'btn-outline' : 'btn-success'}`}
                                            onClick={() => toggleVerified(item)}
                                        >
                                            {item.isVerified ? t('admin.itemsRevokeBtn') : t('admin.itemsApproveBtn')}
                                        </button>
                                        <button className="btn btn-danger btn-xs" onClick={() => handleDelete(item.id)}>{t('admin.usersDeleteBtn')}</button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}
            {total > 1 && (
                <div className="pagination">
                    <button className="btn btn-outline btn-sm" disabled={page === 0} onClick={() => load(page - 1)}>← Назад</button>
                    <span className="pagination-info">Сторінка {page + 1} з {total}</span>
                    <button className="btn btn-outline btn-sm" disabled={page >= total - 1} onClick={() => load(page + 1)}>Далі →</button>
                </div>
            )}
        </div>
    );
};

// ── Verifications Tab ────────────────────────────────────
const VerificationsTab = () => {
    const { t } = useLanguage();
    const [profiles, setProfiles] = useState<PendingProfileResponse[]>([]);
    const [total, setTotal] = useState(0);
    const [page, setPage] = useState(0);
    const [loading, setLoading] = useState(true);
    const [msg, setMsg] = useState('');

    const load = (p = 0) => {
        setLoading(true);
        profileApi.getPendingProfiles(p, 20)
            .then(data => { setProfiles(data.content); setTotal(data.totalPages); setPage(p); })
            .catch(() => setMsg('Не вдалося завантажити запити.'))
            .finally(() => setLoading(false));
    };

    useEffect(() => { load(); }, []);

    const updateStatus = async (userId: number, status: VerificationStatus) => {
        try {
            await profileApi.updateVerificationStatus(userId, status);
            setMsg('');
            load(page);
        } catch { setMsg('Дію не виконано.'); }
    };

    return (
        <div className="admin-tab">
            {msg && <div className="alert alert-error">{msg}</div>}
            {loading ? <div className="spinner" /> : profiles.length === 0
                ? <div className="empty-state"><div className="empty-icon">✅</div><p>{t('admin.verifNoRequests')}</p></div>
                : (
                    <div className="admin-table-wrap">
                        <table className="admin-table">
                            <thead>
                                <tr><th>ID</th><th>Email</th><th>Ім&apos;я</th><th>{t('admin.verifColPhone')}</th><th>{t('admin.verifColBirth')}</th><th>{t('admin.verifColStatus')}</th><th>{t('admin.usersColActions')}</th></tr>
                            </thead>
                            <tbody>
                                {profiles.map(p => (
                                    <tr key={p.userId}>
                                        <td>{p.userId}</td>
                                        <td>{p.email}</td>
                                        <td>{p.firstName} {p.lastName} {p.middleName ?? ''}</td>
                                        <td>{p.phoneNumber ?? '—'}</td>
                                        <td>{p.birthDate ? String(p.birthDate) : '—'}</td>
                                        <td><span className="badge badge-warning">{t('verificationStatus.' + p.verificationStatus)}</span></td>
                                        <td className="action-cell">
                                            <button className="btn btn-success btn-xs" onClick={() => updateStatus(p.userId, 'VERIFIED')}>{t('admin.itemsApproveBtn')}</button>
                                            <button className="btn btn-danger btn-xs" onClick={() => updateStatus(p.userId, 'REJECTED')}>{t('admin.verifRejectBtn')}</button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}
            {total > 1 && (
                <div className="pagination">
                    <button className="btn btn-outline btn-sm" disabled={page === 0} onClick={() => load(page - 1)}>← Назад</button>
                    <span className="pagination-info">Сторінка {page + 1} з {total}</span>
                    <button className="btn btn-outline btn-sm" disabled={page >= total - 1} onClick={() => load(page + 1)}>Далі →</button>
                </div>
            )}
        </div>
    );
};

// ── Categories Tab ───────────────────────────────────────
const CategoriesTab = () => {
    const { t } = useLanguage();
    const [categories, setCategories] = useState<CategoryResponse[]>([]);
    const [loading, setLoading] = useState(true);
    const [msg, setMsg] = useState('');
    const [editing, setEditing] = useState<CategoryResponse | null>(null);
    const [form, setForm] = useState<CategoryRequest>({ name: '', slug: '', iconUrl: null, parentId: null });
    const [showForm, setShowForm] = useState(false);

    const load = () => {
        setLoading(true);
        categoriesApi.getCategories()
            .then(setCategories)
            .catch(() => setMsg('Не вдалося завантажити категорії.'))
            .finally(() => setLoading(false));
    };

    useEffect(() => { load(); }, []);

    const openCreate = () => { setEditing(null); setForm({ name: '', slug: '', iconUrl: null, parentId: null }); setShowForm(true); };
    const openEdit = (c: CategoryResponse) => {
        setEditing(c);
        setForm({ name: c.name, slug: c.slug, iconUrl: c.iconUrl, parentId: c.parentId });
        setShowForm(true);
    };

    const handleSave = async (e: FormEvent) => {
        e.preventDefault();
        try {
            if (editing) await categoriesApi.updateCategory(editing.id, form);
            else await categoriesApi.createCategory(form);
            setShowForm(false);
            setMsg('');
            load();
        } catch { setMsg('Не вдалося зберегти.'); }
    };

    const handleDelete = async (id: number) => {
        if (!confirm(t('admin.catConfirmDelete'))) return;
        try { await categoriesApi.deleteCategory(id); setMsg(''); load(); } catch { setMsg('Не вдалося видалити.'); }
    };

    const flat = (cats: CategoryResponse[], depth = 0): { cat: CategoryResponse; depth: number }[] =>
        cats.flatMap(c => [{ cat: c, depth }, ...flat(c.subcategories, depth + 1)]);

    return (
        <div className="admin-tab">
            {msg && <div className="alert alert-error">{msg}</div>}

            <button className="btn btn-primary btn-sm" style={{ marginBottom: '1rem' }} onClick={openCreate}>
                {t('admin.catNewBtn')}
            </button>

            {showForm && (
                <div className="cat-form-card">
                    <h2 className="section-heading">{editing ? 'Редагувати' : 'Створити'} категорію</h2>
                    <form className="item-form" onSubmit={handleSave}>
                        <div className="form-row">
                            <div className="form-group">
                                <label className="form-label" htmlFor="cf-name">{t('admin.catNameLabel')}</label>
                                <input id="cf-name" className="form-input" value={form.name}
                                    onChange={e => setForm(f => ({ ...f, name: e.target.value }))} required />
                            </div>
                            <div className="form-group">
                                <label className="form-label" htmlFor="cf-slug">{t('admin.catSlugLabel')}</label>
                                <input id="cf-slug" className="form-input" value={form.slug}
                                    onChange={e => setForm(f => ({ ...f, slug: e.target.value }))} required />
                            </div>
                        </div>
                        <div className="form-row">
                            <div className="form-group">
                                <label className="form-label" htmlFor="cf-icon">{t('admin.catIconLabel')}</label>
                                <input id="cf-icon" className="form-input" value={form.iconUrl ?? ''}
                                    onChange={e => setForm(f => ({ ...f, iconUrl: e.target.value || null }))} />
                            </div>
                            <div className="form-group">
                                <label className="form-label" htmlFor="cf-parent">{t('admin.catParentLabel')}</label>
                                <select id="cf-parent" className="form-input"
                                    value={form.parentId ?? ''}
                                    onChange={e => setForm(f => ({ ...f, parentId: e.target.value ? Number(e.target.value) : null }))}>
                                    <option value="">{t('admin.catNoParent')}</option>
                                    {flat(categories)
                                        .filter(({ cat }) => cat.id !== editing?.id)
                                        .map(({ cat, depth }) => (
                                            <option key={cat.id} value={cat.id}>{'  '.repeat(depth)}{cat.name}</option>
                                        ))}
                                </select>
                            </div>
                        </div>
                        <div style={{ display: 'flex', gap: '0.75rem' }}>
                            <button type="submit" className="btn btn-primary btn-sm">{t('admin.catSaveBtn')}</button>
                            <button type="button" className="btn btn-outline btn-sm" onClick={() => setShowForm(false)}>{t('admin.itemsRevokeBtn')}</button>
                        </div>
                    </form>
                </div>
            )}

            {loading ? <div className="spinner" /> : (
                <div className="admin-table-wrap">
                    <table className="admin-table">
                        <thead><tr><th>{t('admin.itemsColId')}</th><th>{t('admin.itemsColTitle')}</th><th>{t('admin.catSlugLabel')}</th><th>{t('admin.catColSubcats')}</th><th>{t('admin.usersColActions')}</th></tr></thead>
                        <tbody>
                            {flat(categories).map(({ cat, depth }) => (
                                <tr key={cat.id}>
                                    <td>{cat.id}</td>
                                    <td style={{ paddingLeft: `${depth * 1.5 + 0.75}rem` }}>{cat.name}</td>
                                    <td><code>{cat.slug}</code></td>
                                    <td>{cat.subcategories.length}</td>
                                    <td className="action-cell">
                                        <button className="btn btn-outline btn-xs" onClick={() => openEdit(cat)}>{t('admin.catEditBtn')}</button>
                                        <button className="btn btn-danger btn-xs" onClick={() => handleDelete(cat.id)}>{t('admin.usersDeleteBtn')}</button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
};

// ── Bookings Tab ─────────────────────────────────────────
const BookingsTab = () => {
    const { t } = useLanguage();
    const [bookings, setBookings] = useState<BookingResponse[]>([]);
    const [total, setTotal] = useState(0);
    const [page, setPage] = useState(0);
    const [loading, setLoading] = useState(true);
    const [msg, setMsg] = useState('');

    const load = (p = 0) => {
        setLoading(true);
        bookingsApi.getAllBookings(p, 15)
            .then(data => { setBookings(data.content); setTotal(data.totalPages); setPage(p); })
            .catch(() => setMsg('Не вдалося завантажити бронювання.'))
            .finally(() => setLoading(false));
    };

    useEffect(() => { load(); }, []);

    const changeStatus = async (booking: BookingResponse, status: BookingStatus) => {
        let cancellationReason: string | undefined;
        if (status === 'CANCELLED') {
            cancellationReason = prompt(t('admin.bookPromptCancel')) ?? undefined;
            if (!cancellationReason) return;
        }
        try {
            await bookingsApi.updateBookingStatus(booking.id, status, cancellationReason);
            setMsg('');
            load(page);
        } catch { setMsg('Не вдалося змінити статус.'); }
    };

    return (
        <div className="admin-tab">
            {msg && <div className="alert alert-error">{msg}</div>}
            {loading ? <div className="spinner" /> : bookings.length === 0
                ? <div className="empty-state"><div className="empty-icon">📅</div><p>{t('admin.bookNoData')}</p></div>
                : (
                    <div className="admin-table-wrap">
                        <table className="admin-table">
                            <thead>
                                <tr>
                                    <th>{t('admin.usersColId')}</th><th>{t('admin.bookColItem')}</th><th>{t('admin.bookColRenter')}</th>
                                    <th>{t('admin.bookColPeriod')}</th><th>{t('admin.bookColTotal')}</th><th>{t('admin.itemsColStatus')}</th><th>{t('admin.usersColActions')}</th>
                                </tr>
                            </thead>
                            <tbody>
                                {bookings.map(b => (
                                    <tr key={b.id}>
                                        <td>{b.id}</td>
                                        <td>#{b.itemId}</td>
                                        <td>#{b.renterId}</td>
                                        <td>{b.startDate} — {b.endDate}</td>
                                        <td>₴{b.totalPrice}</td>
                                        <td><span className="badge badge-neutral">{b.status}</span></td>
                                        <td>
                                            <select
                                                className="role-select"
                                                value={b.status}
                                                onChange={e => changeStatus(b, e.target.value as BookingStatus)}
                                            >
                                                {BOOKING_STATUSES.map(s => (
                                                    <option key={s} value={s}>{s}</option>
                                                ))}
                                            </select>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}
            {total > 1 && (
                <div className="pagination">
                    <button className="btn btn-outline btn-sm" disabled={page === 0} onClick={() => load(page - 1)}>← Назад</button>
                    <span className="pagination-info">Сторінка {page + 1} з {total}</span>
                    <button className="btn btn-outline btn-sm" disabled={page >= total - 1} onClick={() => load(page + 1)}>Далі →</button>
                </div>
            )}
        </div>
    );
};

// ── Main Admin Page ───────────────────────────────────────
const AdminPage = () => {
    const { t } = useLanguage();
    const [tab, setTab] = useState<Tab>('stats');

    const tabs: { key: Tab; label: string }[] = [
        { key: 'stats', label: t('admin.tabStats') },
        { key: 'users', label: t('admin.tabUsers') },
        { key: 'items', label: t('admin.tabItems') },
        { key: 'verifications', label: t('admin.tabVerifications') },
        { key: 'categories', label: t('admin.tabCategories') },
        { key: 'bookings', label: t('admin.tabBookings') },
    ];

    return (
        <div className="page">
            <div className="page-header">
                <h1 className="page-title">{t('admin.title')}</h1>
            </div>
            <div className="admin-tabs">
                {tabs.map(t => (
                    <button
                        key={t.key}
                        className={`admin-tab-btn ${tab === t.key ? 'active' : ''}`}
                        onClick={() => setTab(t.key)}
                    >
                        {t.label}
                    </button>
                ))}
            </div>
            <div className="admin-content">
                {tab === 'stats' && <StatsTab />}
                {tab === 'users' && <UsersTab />}
                {tab === 'items' && <ItemsTab />}
                {tab === 'verifications' && <VerificationsTab />}
                {tab === 'categories' && <CategoriesTab />}
                {tab === 'bookings' && <BookingsTab />}
            </div>
        </div>
    );
};

export default AdminPage;
