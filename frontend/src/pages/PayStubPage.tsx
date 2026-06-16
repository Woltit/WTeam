import { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import paymentsApi from '../api/payments';

const PayStubPage = () => {
    const { paymentId } = useParams<{ paymentId: string }>();
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    const handleAction = async (success: boolean) => {
        if (!paymentId) return;
        setLoading(true);
        try {
            await paymentsApi.callbackStub(Number(paymentId), success);
            navigate('/my-bookings'); // Redirect back to bookings after payment
        } catch (err: any) {
            setError('Error processing payment stub');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="page" style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '60vh' }}>
            <div style={{ maxWidth: '400px', width: '100%', padding: '2rem', backgroundColor: '#fff', borderRadius: '12px', boxShadow: '0 4px 12px rgba(0,0,0,0.1)', textAlign: 'center' }}>
                <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>💳</div>
                <h2>LiqPay (Stub)</h2>
                <p style={{ color: '#666', marginBottom: '2rem' }}>
                    This is a stub payment page. Choose whether the payment should succeed or fail.
                </p>
                {error && <div className="alert alert-error" style={{ marginBottom: '1rem' }}>{error}</div>}
                
                <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                    <button 
                        className="btn btn-primary" 
                        onClick={() => handleAction(true)}
                        disabled={loading}
                    >
                        {loading ? 'Processing...' : 'Simulate SUCCESS'}
                    </button>
                    <button 
                        className="btn btn-outline btn-danger" 
                        onClick={() => handleAction(false)}
                        disabled={loading}
                    >
                        {loading ? 'Processing...' : 'Simulate FAILURE'}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default PayStubPage;
