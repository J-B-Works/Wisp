import React, { useState, useEffect } from 'react'; // <-- Adicionamos o useEffect
import { NavbarPrincipal } from '../../components/NavBar/NavBar.jsx';
import { useNavigate } from 'react-router-dom';
import './Perfil.css'; 
import '../../index.css';


export function PerfilUC() {

  const navigate = useNavigate();
  
  const [usuario, setUsuario] = useState({
    nome: 'Carregando...',
    email: '',
    idade: '',
    cep: '',
    interesses: [] 
  });

  useEffect(() => {
    const buscarPerfilDoGrafo = async () => {
      try {
        const userID = localStorage.getItem('wisp_userId');
        if (!userID) return;

        // Busca os dados fresquinhos do Grafo via API
        const resposta = await fetch(`${import.meta.env.VITE_API_URL}/usuarios/${userID}`);
        if (resposta.ok) {
          const dadosJson = await resposta.json();
          setUsuario(dadosJson);
        }
      } catch (erro) {
        console.error("Erro ao buscar perfil do backend:", erro);
      }
    };

    buscarPerfilDoGrafo();
  }, []);
  
  return (
    <div className="perfil-page">
      <NavbarPrincipal mostrarBusca={false} mostrarHamburger={true} />

      <div className="perfil-container">

        {/* ============================== */}
        {/* CARD 1: INFORMAÇÕES PESSOAIS */}
        {/* ============================== */}
        <div className="perfil-card">
          <h2>Informações pessoais:</h2>
          <button className="btn-editar" onClick={() => navigate('/editar-perfil')}>
            <span style={{ fontSize: '1.2rem' }}>📝</span> Editar
          </button>

          <div className="info-conteudo">
            <p className="info-linha"><strong>Nome:</strong> {usuario.nome}</p>
            <p className="info-linha"><strong>Email:</strong> {usuario.email}</p>
            <p className="info-linha"><strong>Idade:</strong> {usuario.idade}</p>
            <p className="info-linha"><strong>Cep:</strong> {usuario.cep}</p>
          </div>
        </div>

        {/* ============================== */}
        {/* CARD 2: PREFERÊNCIAS */}
        {/* ============================== */}
        {/* CARD 2: PREFERÊNCIAS */}
        <div className="perfil-card">
          <h2 style={{ textAlign: 'center' }}>Preferências:</h2>
          
          {/* A MÁGICA: Navegamos para o Cadastro, mas passamos um "aviso" escondido! */}
          <button 
            className="btn-editar" 
            onClick={() => navigate('/cadastro', { state: { editarPreferencias: true } })}
          >
            <span style={{ fontSize: '1.2rem' }}>📝</span> Editar
          </button>

          <div className="preferencias-grid">
            {/* Usamos a interrogação (?) por segurança, caso os interesses demorem a carregar */}
            {usuario.interesses?.map((interesse) => (
              <div key={interesse} className="tag-preferencia">
                {interesse}
              </div>
            ))}
          </div>
        </div>

      </div>
    </div>
  );
}

export default PerfilUC;