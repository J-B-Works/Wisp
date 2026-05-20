import React, { useState } from 'react';
import '../../index.css'; 
import { useNavigate } from 'react-router-dom';

export function Login() {

  // COLOQUE ESTA LINHA AQUI:
  const navigate = useNavigate();

  // Estados para guardar o que o usuário digita
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');

  // Funções dos botões
  const handleLogin = async () => {
    try {
      // REACT ---> JAVA
      const resposta = await fetch(import.meta.env.VITE_API_URL + '/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: email }) // Envia o email digitado pelo usuário no useState
      });

      // REACT <--- JAVA
      const idDoUsuario = await resposta.text();

      if (idDoUsuario !== "ERRO") { // Se a resposta NÃO for ERRO, o usuário pode "logar"
        localStorage.setItem('wisp_userId', idDoUsuario); // Setta wisp_userId atual para ser o id desse usuário
        navigate('/feed');          // Gera recomendações com base nesse usuário
      } else {                      // Se a resposta FOR ERRO, então o usuário "não existe"/não tinha se cadastrado antes
        alert("Email não encontrado! Faça seu cadastro.");
      }
    } catch (erro) {
      console.error("Erro no servidor:", erro);
      alert("Erro ao tentar conectar com o servidor.");
    }
  };

  // ARRUMADO: O nome aqui estava repetido!
  const handleCadastro = () => {
    navigate('/cadastro');
  };

  const handleSemLogin = () => {
    localStorage.removeItem('wisp_userId'); // Assegurando de que o usuário visitante não tem um ID
    navigate('/feed');                      // Vai direto para o feed
  };

  return (
    // CONTAINER PRINCIPAL (Fundo bege limpo, tudo centralizado)
    <div style={{ 
      backgroundColor: 'var(--bg-bege)', 
      minHeight: '100vh', 
      display: 'flex', 
      flexDirection: 'column', 
      alignItems: 'center', 
      justifyContent: 'center',
      padding: '20px',
      fontFamily: 'sans-serif'
    }}>

      {/* LOGO */}
      <div style={{
        width: '90px',
        height: '90px',
        backgroundColor: 'var(--accent-azul-claro)', // Azul da sua paleta
        borderRadius: '50%',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        fontWeight: 'bold',
        fontSize: '1.2rem',
        marginBottom: '20px'
      }}>
        Logo
      </div>

      {/* TÍTULOS */}
      <h1 style={{ color: 'var(--bordas)', fontSize: '2.5rem', margin: '0 0 10px 0' }}>
        Bem Vindo!
      </h1>
      <p style={{ color: 'var(--bordas)', margin: '0 0 25px 0', fontSize: '1rem' }}>
        Entre ou cadastre-se receber recomendações
      </p>

      {/* CARD DO FORMULÁRIO */}
      <div style={{
        backgroundColor: '#98C5D9', // Azul claro acinzentado do print
        border: '2px solid var(--bordas)',
        borderRadius: '12px',
        padding: '30px',
        width: '100%',
        maxWidth: '380px',
        display: 'flex',
        flexDirection: 'column',
        gap: '20px',
        boxSizing: 'border-box'
      }}>
        
        {/* CAMPO EMAIL */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '5px', textAlign: 'left' }}>
          <label style={{ fontWeight: 'bold', fontSize: '0.95rem' }}>Email</label>
          <input 
            type="email" 
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            style={{
              width: '100%',
              padding: '12px',
              borderRadius: '8px',
              border: '2px solid var(--bordas)',
              backgroundColor: '#98C5D9', // Fundo mesclando com o card
              boxSizing: 'border-box',
              outline: 'none',
              fontSize: '1rem'
            }}
          />
        </div>

        {/* CAMPO SENHA */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '5px', textAlign: 'left' }}>
          <label style={{ fontWeight: 'bold', fontSize: '0.95rem' }}>Senha</label>
          <input 
            type="password" 
            value={senha}
            onChange={(e) => setSenha(e.target.value)}
            style={{
              width: '100%',
              padding: '12px',
              borderRadius: '8px',
              border: '2px solid var(--bordas)',
              backgroundColor: '#98C5D9', 
              boxSizing: 'border-box',
              outline: 'none',
              fontSize: '1rem'
            }}
          />
        </div>

        {/* BOTÃO ENTRE */}
        <button onClick={handleLogin} style={{
          backgroundColor: 'var(--card-blue)', // Azul escuro
          color: 'white',
          padding: '12px',
          borderRadius: '8px',
          border: '2px solid var(--bordas)',
          fontWeight: 'bold',
          fontSize: '1.1rem',
          cursor: 'pointer',
          marginTop: '5px'
        }}>
          Entre
        </button>

        {/* BOTÃO CADASTRE-SE */}
        <button onClick={handleCadastro} style={{
          backgroundColor: '#F1D888', // Amarelo/Bege
          color: 'var(--bordas)',
          padding: '12px',
          borderRadius: '8px',
          border: '2px solid var(--bordas)',
          fontWeight: 'bold',
          fontSize: '1.1rem',
          cursor: 'pointer'
        }}>
          Cadastre-se
        </button>

      </div>

      {/* LINKS INFERIORES */}
      <div style={{ marginTop: '15px', display: 'flex', flexDirection: 'column', gap: '5px', alignItems: 'center' }}>
        <span style={{ fontWeight: 'bold', fontSize: '0.9rem' }}>Ou</span>
        
        <span 
          onClick={handleSemLogin}
          style={{ 
            color: '#B23A3A', // Vermelho
            fontWeight: 'bold', 
            textDecoration: 'underline',
            cursor: 'pointer',
            fontSize: '1rem'
          }}
        >
          Continue sem Login
        </span>
      </div>

    </div>
  );
}

export default Login;