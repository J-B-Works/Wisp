import React, { useState, useEffect } from 'react'; // <-- Adicionamos o useEffect
import { NavbarPrincipal } from '../../assets/NavBar/navbar.jsx';
import { useNavigate } from 'react-router-dom';
import './Style.css'; 
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

  // 👇 ESSE BLOCO PRECISA ESTAR AQUI 👇
  useEffect(() => {
    // Procura os dados que o Cadastro acabou de salvar
    const dadosSalvos = localStorage.getItem('usuarioWisp');

    if (dadosSalvos) {
      // Se achou, atualiza a tela!
      setUsuario(JSON.parse(dadosSalvos));
    } else {
      console.log("Nenhum usuário encontrado na memória.");
    }
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