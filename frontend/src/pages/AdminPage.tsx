import { useEffect, useState, type FormEvent } from 'react';
import usersApi from '../api/users';
import itemsApi from '../api/items';
import profileApi from '../api/profile';
import categoriesApi from '../api/categories';
import type { UserResponse, Role, VerificationStatus } from '../types/user';
import type { ItemResponse } from '../types/item';
import type { UserProfileResponse } from '../types/user';
import type { CategoryResponse, CategoryRequest } from '../types/category';

type Tab = 'users' | 'items' | 'verifications' | 'categories';

// ── Users Tab ────────────────────────────────────────────
const UsersTab = () => {
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
            .catch(() => setMsg('Failed to load users.'))
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
        } catch { setMsg('User not found.'); setUsers([]); }
        finally { setLoading(false); }
    };

    const doAction = async (action: () => Promise<void>) => {
        try { await action(); load(page); } catch { setMsg('Action failed.'); }
    };

    return (
        <div className="admin-tab">
            <form className="admin-search" onSubmit={handleSearch}>
                <input className="form-input" placeholder="Search by email…" value={search} onChange={e => setSearch(e.target.value)} />
                <button className="btn btn-outline btn-sm" type="submit">Search</button>
                <button className="btn btn-outline btn-sm" type="button" onClick={() => { setSearch(''); load(); }}>Clear</button>
            </form>

            {msg && <div className="alert alert-error">{msg}</div>}

            {loading ? <div className="spinner" /> : (
                <div className="admin-table-wrap">
                    <table className="admin-table">
                        <thead>
                            <tr>
                                <th>ID</th><th>Email</th><th>Name</th><th>Role</th><th>Status</th><th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {users.map(u => (
                                <tr key={u.id}>
                                    <td>{u.id}</td>
                                    <td>{u.email}</td>
                                    <td>{u.profile.firstName} {u.profile.lastName}</td>
                                    <td>
                                        <select className="role-select"
                                            value={u.role}
                                            onChange={e => doAction(() => usersApi.updateRole(u.id, e.target.value as Role))}>
                                            {(['USER', 'MODER', 'ADMIN'] as Role[]).map(r => <option key={r} value={r}>{r}</option>)}
                                        </select>
                                    </td>
                                    <td>
                                        <span className={`badge ${u.profile.verificationStatus === 'VERIFIED' ? 'badge-success' : 'badge-neutral'}`}>
                                            {u.profile.verificationStatus}
                                        </span>
                                    </td>
                                    <td className="action-cell">
                                        <button className="btn btn-outline btn-xs"
                                            onClick={() => doAction(() => usersApi.activateUser(u.id))}>
                                            Activate
                                        </button>
                                        <button className="btn btn-warning btn-xs"
                                            onClick={() => {
                                                const reason = prompt('Block reason:');
                                                if (reason) doAction(() => usersApi.blockUser(u.id, { reason }));
                                            }}>
                                            Block
                                        </button>
                                        <button className="btn btn-danger btn-xs"
                                            onClick={() => {
                                                if (confirm(`Delete user ${u.email}?`))
                                                    doAction(() => usersApi.deleteUser(u.id));
                                            }}>
                                            Delete
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
                    <button className="btn btn-outline btn-sm" disabled={page === 0} onClick={() => load(page - 1)}>← Prev</button>
                    <span className="pagination-info">Page {page + 1}</span>
                    <button className="btn btn-outline btn-sm" disabled={page >= total - 1} onClick={() => load(page + 1)}>Next →</button>
                </div>
            )}
        </div>
    );
};

// ── Items Tab ────────────────────────────────────────────
const ItemsTab = () => {
    const [items, setItems] = useState<ItemResponse[]>([]);
    const [total, setTotal] = useState(0);
    const [page, setPage] = useState(0);
    const [loading, setLoading] = useState(true);
    const [msg, setMsg] = useState('');

    const load = (p = 0) => {
        setLoading(true);
        itemsApi.getAllItems(p, 15)
            .then(data => { setItems(data.content); setTotal(data.totalPages); setPage(p); })
            .catch(() => setMsg('Failed to load items.'))
            .finally(() => setLoading(false));
    };

    useEffect(() => { load(); }, []);

    const handleDelete = async (id: number) => {
        if (!confirm('Delete this item?')) return;
        try { await itemsApi.deleteItem(id); load(page); } catch { setMsg('Delete failed.'); }
    };

    return (
        <div className="admin-tab">
            {msg && <div className="alert alert-error">{msg}</div>}
            {loading ? <div className="spinner" /> : (
                <div className="admin-table-wrap">
                    <table className="admin-table">
                        <thead>
                            <tr><th>ID</th><th>Title</th><th>Owner</th><th>City</th><th>Status</th><th>Price/Day</th><th>Actions</th></tr>
                        </thead>
                        <tbody>
                            {items.map(item => (
                                <tr key={item.id}>
                                    <td>{item.id}</td>
                                    <td>{item.title}</td>
                                    <td>{item.ownerProfile.firstName} {item.ownerProfile.lastName}</td>
                                    <td>{item.city}</td>
                                    <td><span className="badge badge-neutral">{item.status}</span></td>
                                    <td>₴{item.pricePerDay}</td>
                                    <td>
                                        <button className="btn btn-danger btn-xs" onClick={() => handleDelete(item.id)}>Delete</button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}
            {total > 1 && (
                <div className="pagination">
                    <button className="btn btn-outline btn-sm" disabled={page === 0} onClick={() => load(page - 1)}>← Prev</button>
                    <span className="pagination-info">Page {page + 1}</span>
                    <button className="btn btn-outline btn-sm" disabled={page >= total - 1} onClick={() => load(page + 1)}>Next →</button>
                </div>
            )}
        </div>
    );
};

// ── Verifications Tab ────────────────────────────────────
const VerificationsTab = () => {
    const [profiles, setProfiles] = useState<UserProfileResponse[]>([]);
    const [loading, setLoading] = useState(true);
    const [msg, setMsg] = useState('');

    const load = () => {
        setLoading(true);
        profileApi.getPendingProfiles()
            .then(data => setProfiles(data.content))
            .catch(() => setMsg('Failed to load pending profiles.'))
            .finally(() => setLoading(false));
    };

    useEffect(() => { load(); }, []);

    const updateStatus = async (userId: number, status: VerificationStatus) => {
        try {
            await profileApi.updateVerificationStatus(userId, status);
            load();
        } catch { setMsg('Action failed.'); }
    };

    return (
        <div className="admin-tab">
            {msg && <div className="alert alert-error">{msg}</div>}
            {loading ? <div className="spinner" /> : profiles.length === 0
                ? <div className="empty-state"><div className="empty-icon">✅</div><p>No pending verifications.</p></div>
                : (
                    <div className="admin-table-wrap">
                        <table className="admin-table">
                            <thead>
                                <tr><th>Name</th><th>Phone</th><th>Birth Date</th><th>Status</th><th>Actions</th></tr>
                            </thead>
                            <tbody>
                                {profiles.map((p, i) => (
                                    <tr key={i}>
                                        <td>{p.firstName} {p.lastName} {p.middleName}</td>
                                        <td>{p.phoneNumber}</td>
                                        <td>{p.birthDate ? String(p.birthDate) : '—'}</td>
                                        <td><span className="badge badge-warning">{p.verificationStatus}</span></td>
                                        <td className="action-cell">
                                            <button className="btn btn-success btn-xs" onClick={() => updateStatus(i + 1, 'VERIFIED')}>Approve</button>
                                            <button className="btn btn-danger btn-xs" onClick={() => updateStatus(i + 1, 'REJECTED')}>Reject</button>
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

// ── Categories Tab ───────────────────────────────────────
const CategoriesTab = () => {
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
            .catch(() => setMsg('Failed to load categories.'))
            .finally(() => setLoading(false));
    };

    useEffect(() => { load(); }, []);

    const openCreate = () => { setEditing(null); setForm({ name: '', slug: '', iconUrl: null, parentId: null }); setShowForm(true); };
    const openEdit = (c: CategoryResponse) => {
        setEditing(c);
        setForm({ name: c.name, slug: c.slug, iconUrl: c.iconUrl, parentId: null });
        setShowForm(true);
    };

    const handleSave = async (e: FormEvent) => {
        e.preventDefault();
        try {
            if (editing) await categoriesApi.updateCategory(editing.id, form);
            else await categoriesApi.createCategory(form);
            setShowForm(false);
            load();
        } catch { setMsg('Save failed.'); }
    };

    const handleDelete = async (id: number) => {
        if (!confirm('Delete category?')) return;
        try { await categoriesApi.deleteCategory(id); load(); } catch { setMsg('Delete failed.'); }
    };

    const flat = (cats: CategoryResponse[], depth = 0): { cat: CategoryResponse; depth: number }[] =>
        cats.flatMap(c => [{ cat: c, depth }, ...flat(c.subcategories, depth + 1)]);

    return (
        <div className="admin-tab">
            {msg && <div className="alert alert-error">{msg}</div>}

            <button className="btn btn-primary btn-sm" style={{ marginBottom: '1rem' }} onClick={openCreate}>
                + New Category
            </button>

            {showForm && (
                <div className="cat-form-card">
                    <h2 className="section-heading">{editing ? 'Edit' : 'Create'} Category</h2>
                    <form className="item-form" onSubmit={handleSave}>
                        <div className="form-row">
                            <div className="form-group">
                                <label className="form-label" htmlFor="cf-name">Name *</label>
                                <input id="cf-name" className="form-input" value={form.name}
                                    onChange={e => setForm(f => ({ ...f, name: e.target.value }))} required />
                            </div>
                            <div className="form-group">
                                <label className="form-label" htmlFor="cf-slug">Slug *</label>
                                <input id="cf-slug" className="form-input" value={form.slug}
                                    onChange={e => setForm(f => ({ ...f, slug: e.target.value }))} required />
                            </div>
                        </div>
                        <div className="form-row">
                            <div className="form-group">
                                <label className="form-label" htmlFor="cf-icon">Icon URL</label>
                                <input id="cf-icon" className="form-input" value={form.iconUrl ?? ''}
                                    onChange={e => setForm(f => ({ ...f, iconUrl: e.target.value || null }))} />
                            </div>
                            <div className="form-group">
                                <label className="form-label" htmlFor="cf-parent">Parent Category</label>
                                <select id="cf-parent" className="form-input"
                                    value={form.parentId ?? ''}
                                    onChange={e => setForm(f => ({ ...f, parentId: e.target.value ? Number(e.target.value) : null }))}>
                                    <option value="">None (top-level)</option>
                                    {flat(categories).map(({ cat, depth }) => (
                                        <option key={cat.id} value={cat.id}>{'  '.repeat(depth)}{cat.name}</option>
                                    ))}
                                </select>
                            </div>
                        </div>
                        <div style={{ display: 'flex', gap: '0.75rem' }}>
                            <button type="submit" className="btn btn-primary btn-sm">Save</button>
                            <button type="button" className="btn btn-outline btn-sm" onClick={() => setShowForm(false)}>Cancel</button>
                        </div>
                    </form>
                </div>
            )}

            {loading ? <div className="spinner" /> : (
                <div className="admin-table-wrap">
                    <table className="admin-table">
                        <thead><tr><th>ID</th><th>Name</th><th>Slug</th><th>Subcategories</th><th>Actions</th></tr></thead>
                        <tbody>
                            {flat(categories).map(({ cat, depth }) => (
                                <tr key={cat.id}>
                                    <td>{cat.id}</td>
                                    <td style={{ paddingLeft: `${depth * 1.5 + 0.75}rem` }}>{cat.name}</td>
                                    <td><code>{cat.slug}</code></td>
                                    <td>{cat.subcategories.length}</td>
                                    <td className="action-cell">
                                        <button className="btn btn-outline btn-xs" onClick={() => openEdit(cat)}>Edit</button>
                                        <button className="btn btn-danger btn-xs" onClick={() => handleDelete(cat.id)}>Delete</button>
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

// ── Main Admin Page ───────────────────────────────────────
const AdminPage = () => {
    const [tab, setTab] = useState<Tab>('users');

    const tabs: { key: Tab; label: string }[] = [
        { key: 'users', label: '👤 Users' },
        { key: 'items', label: '📦 Items' },
        { key: 'verifications', label: '✅ Verifications' },
        { key: 'categories', label: '🗂 Categories' },
    ];

    return (
        <div className="page">
            <div className="page-header">
                <h1 className="page-title">Admin Panel</h1>
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
                {tab === 'users' && <UsersTab />}
                {tab === 'items' && <ItemsTab />}
                {tab === 'verifications' && <VerificationsTab />}
                {tab === 'categories' && <CategoriesTab />}
            </div>
        </div>
    );
};

export default AdminPage;
