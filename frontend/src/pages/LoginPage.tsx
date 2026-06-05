import {useState} from "react";
import {useNavigate} from "react-router";
import {useDispatch} from "react-redux";
import {setCredentials} from "../store/slices/authSlice.ts";

function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState('')

  const navigate = useNavigate();
  const dispatch = useDispatch();

  const handleSubmit = async (e: { preventDefault: () => void; }) => {
    e.preventDefault();
    try {
        const { user, token, refreshToken } = await login({ email, password });
        dispatch(setCredentials({ user, token, refreshToken }));
        navigate("/");
    } catch (error) {
      setError("Помилка входу. Перевірте email та пароль.");
    }
  };

  return (
    <div>
      <h1>Вхід</h1>
      <form onSubmit={handleSubmit}>
        {error && <p>{error}</p>}
        <div>
          <label htmlFor="email">Email:</label>
          <input
            type="email"
            placeholder="Введіть email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
        </div>
        <div>
          <label htmlFor="password">Пароль:</label>
          <input
            type="password"
            placeholder="Введіть пароль"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </div>
        <button type="submit">Вхід</button>
      </form>
    </div>
  );
}

export default LoginPage    


