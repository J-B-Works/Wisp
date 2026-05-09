import React, { useState } from 'react';
import { NavbarPrincipal } from '../../assets/NavBar/navbar.jsx';
import './Style.css'; // Importando nosso novo CSS
import '../../index.css';

export function PerfilUC() {
  
  // MOCK DE DADOS: Simulando o que vai vir do Banco de Dados (Java)
  const [usuario, setUsuario] = useState({
    nome: 'Júlia Andrade',
    email: '10428513@mackenzista.com.br',
    idade: 21,
    cep: '00000-000',
    interesses: ['Cursos', 'Oficinas', 'Artes Visuais', 'Tecnologia', 'Moda']
  });

  return (
    <div className="perfil-page">
      
      {/* Navbar Padrão (Sem barra de busca se quiser, igual ao print) */}
      <NavbarPrincipal mostrarBusca={false} mostrarHamburger={true} />

      <div className="perfil-container">

        {/* ============================== */}
        {/* CARD 1: INFORMAÇÕES PESSOAIS */}
        {/* ============================== */}
        <div className="perfil-card">
          <h2>Informações pessoais:</h2>
          
          <button className="btn-editar">
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
        <div className="perfil-card">
          
          {/* Título centralizado com style inline para não afetar o outro card */}
          <h2 style={{ textAlign: 'center' }}>Preferências:</h2>
          
          <button className="btn-editar">
            <span style={{ fontSize: '1.2rem' }}>📝</span> Editar
          </button>

          {/* O React desenha um botão para cada item da lista "interesses" */}
          <div className="preferencias-grid">
            {usuario.interesses.map((interesse) => (
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