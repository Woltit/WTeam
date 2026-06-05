import { useEffect, useState, useRef, type FormEvent, type ChangeEvent } from 'react';
import profileApi from '../api/profile';
import { useAuth } from '../contexts/AuthContext';
import type { UserProfileResponse } from '../types/user';

const ProfilePage = () => {
    const { user } = useAuth();
    const [profile, setProfile] = useState<UserProfileResponse | null>(null);
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [avatarUploading, setAvatarUploading] = useState(false);
    const fileRef = useRef<HTMLInputElement>(null);

    const [form, setForm] = useState({
        firstName: '',
        lastName: '',
        middleName: '',
        birthDate: '',
        phoneNumber: '',
        bio: '',
    });

    useEffect(() => {
        profileApi.getMyProfile()
            .then(data => {
                setProfile(data);
                setForm({
                    firstName: data.firstName ?? '',
                    lastName: data.lastName ?? '',
                    middleName: data.middleName ?? '',
                    birthDate: data.birthDate ? String(data.birthDate) : '',
                    phoneNumber: data.phoneNumber ?? '',
                    bio: data.bio ?? '',
                });
            })
            .catch(() => setError('Не вдалося завантажити профіль.'))
            .finally(() => setLoading(false));
    }, []);

    const set = (field: string, value: string) =>
        setForm(f => ({ ...f, [field]: value }));

    const handleSave = async (e: FormEvent) => {
        e.preventDefault();
        setError(''); setSuccess('');
        setSaving(true);
        try {
            const updated = await profileApi.updateMyProfile({
                firstName: form.firstName,
                lastName: form.lastName,
                middleName: form.middleName || null,
                birthDate: form.birthDate,
                phoneNumber: form.phoneNumber,
                bio: form.bio || null,
            });
            setProfile(updated);
            setSuccess('Профіль успішно оновлено!');
        } catch (err: unknown) {
            const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message;
            setError(msg ?? 'Не вдалося оновити профіль.');
        } finally {
            setSaving(false);
        }
    };

    const handleAvatarChange = async (e: ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0];
        if (!file) return;
        setAvatarUploading(true);
        setError(''); setSuccess('');
        try {
            await profileApi.uploadAvatar(file);
            const updated = await profileApi.getMyProfile();
            setProfile(updated);
            setSuccess('Avatar updated!');
        } catch (err: unknown) {
            const status = (err as { response?: { status?: number } })?.response?.status;
            setError(status === 500 || status === 501
                ? 'Завантаження аватара тимчасово недоступне.'
                : 'Не вдалося завантажити аватар.');
        } finally {
            setAvatarUploading(false);
        }
    };

    const verificationColor: Record<string, string> = {
        VERIFIED: 'badge-success',
        PENDING: 'badge-warning',
        UNVERIFIED: 'badge-neutral',
        REJECTED: 'badge-error',
    };

    const verificationLabel: Record<string, string> = {
        VERIFIED: 'Верифіковано',
        PENDING: 'Очікує перевірки',
        UNVERIFIED: 'Не верифіковано',
        REJECTED: 'Відхилено',
    };

    const handleSubmitVerification = async () => {
        setError(''); setSuccess('');
        try {
            const updated = await profileApi.submitForVerification();
            setProfile(updated);
            setSuccess('Запит на верифікацію надіслано!');
        } catch (err: unknown) {
            const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message;
            setError(msg ?? 'Не вдалося надіслати запит на верифікацію.');
        }
    };

    if (loading) return <div className="page-loader"><div className="spinner" /></div>;

    return (
        <div className="page">
            <div className="page-header">
                <h1 className="page-title">Мій профіль</h1>
            </div>

            <div className="profile-layout">
                {/* Left — avatar + stats */}
                <div className="profile-sidebar">
                    <div className="avatar-section">
                        <div className="profile-avatar" onClick={() => fileRef.current?.click()}>
                            {profile?.avatarUrl
                                ? <img src={profile.avatarUrl} alt="avatar" />
                                : <span className="avatar-initials">
                                    {user?.profile?.firstName?.[0]}{user?.profile?.lastName?.[0]}
                                </span>
                            }
                            {avatarUploading && <div className="avatar-overlay"><div className="spinner" /></div>}
                            <div className="avatar-edit-hint">Змінити</div>
                        </div>
                        <input
                            ref={fileRef}
                            type="file"
                            accept="image/*"
                            className="hidden"
                            id="avatar-upload"
                            onChange={handleAvatarChange}
                        />
                    </div>

                    <div className="profile-meta">
                        <div className="profile-email">{user?.email}</div>
                        <span className={`badge ${verificationColor[profile?.verificationStatus ?? 'UNVERIFIED']}`}>
                            {verificationLabel[profile?.verificationStatus ?? 'UNVERIFIED']}
                        </span>
                    </div>

                    <div className="profile-stats">
                        <div className="stat-item">
                            <span className="stat-val">{profile?.totalSuccessfulRents ?? 0}</span>
                            <span className="stat-label">Успішних оренд</span>
                        </div>
                        {profile?.renterTrustScore != null && (
                            <div className="stat-item">
                                <span className="stat-val">⭐ {profile.renterTrustScore}</span>
                                <span className="stat-label">Рейтинг орендаря</span>
                            </div>
                        )}
                        {profile?.ownerTrustScore != null && (
                            <div className="stat-item">
                                <span className="stat-val">⭐ {profile.ownerTrustScore}</span>
                                <span className="stat-label">Рейтинг власника</span>
                            </div>
                        )}
                    </div>
                </div>

                {/* Right — edit form */}
                <div className="profile-form-section">
                    {error && <div className="alert alert-error">{error}</div>}
                    {success && <div className="alert alert-success">{success}</div>}

                    <form className="item-form" onSubmit={handleSave}>
                        <div className="form-row">
                            <div className="form-group">
                                <label className="form-label" htmlFor="pf-first">Ім&apos;я *</label>
                                <input id="pf-first" className="form-input" value={form.firstName}
                                    onChange={e => set('firstName', e.target.value)} required />
                            </div>
                            <div className="form-group">
                                <label className="form-label" htmlFor="pf-last">Прізвище *</label>
                                <input id="pf-last" className="form-input" value={form.lastName}
                                    onChange={e => set('lastName', e.target.value)} required />
                            </div>
                        </div>

                        <div className="form-row">
                            <div className="form-group">
                                <label className="form-label" htmlFor="pf-middle">По батькові</label>
                                <input id="pf-middle" className="form-input" value={form.middleName}
                                    onChange={e => set('middleName', e.target.value)} />
                            </div>
                            <div className="form-group">
                                <label className="form-label" htmlFor="pf-birth">Дата народження *</label>
                                <input id="pf-birth" type="date" className="form-input" value={form.birthDate}
                                    onChange={e => set('birthDate', e.target.value)} required />
                            </div>
                        </div>

                        <div className="form-group">
                            <label className="form-label" htmlFor="pf-phone">Телефон *</label>
                            <input id="pf-phone" type="tel" className="form-input" value={form.phoneNumber}
                                onChange={e => set('phoneNumber', e.target.value)}
                                required placeholder="+380XXXXXXXXX" />
                        </div>

                        <div className="form-group">
                            <label className="form-label" htmlFor="pf-bio">Про себе</label>
                            <textarea id="pf-bio" className="form-input form-textarea" rows={3}
                                value={form.bio} onChange={e => set('bio', e.target.value)}
                                placeholder="Розкажіть трохи про себе..." />
                        </div>

                        <div style={{ display: 'flex', gap: '0.75rem', flexWrap: 'wrap' }}>
                            <button type="submit" className="btn btn-primary" disabled={saving}>
                                {saving ? <span className="spinner-sm" /> : 'Зберегти профіль'}
                            </button>
                            {profile && (profile.verificationStatus === 'UNVERIFIED' || profile.verificationStatus === 'REJECTED') && (
                                <button type="button" className="btn btn-outline" onClick={handleSubmitVerification}>
                                    Надіслати на верифікацію
                                </button>
                            )}
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default ProfilePage;
