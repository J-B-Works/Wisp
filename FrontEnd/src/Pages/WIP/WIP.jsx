import React from 'react';
import { NavbarPrincipal } from '../../assets/NavBar/navbar.jsx';
import './WIP.css';

// Se você já tiver o fantasminha marrom exportado do Figma, importe ele aqui!
// import iconFantasminha from '../../assets/fantasma.png'; 

export function WIP() {
  return (
    <div className="wip-page">
      
      {/* Navbar Padrão do Feed */}
      <NavbarPrincipal mostrarBusca={true} mostrarHamburger={true} />

      {/* As manchas do fundo */}
      <div className="blob blob-vermelho"></div>
      <div className="blob blob-amarelo"></div>
      <div className="blob blob-laranja"></div>

      {/* O conteúdo central */}
      <div className="wip-content">
        
        <div className="wip-icone">
          {/* Se tiver a imagem, troque o emoji por: <img src={iconFantasminha} alt="Logo Wisp" width="120" /> */}
          👻
        </div>
        
        <h1 className="wip-titulo">Disponível em Breve!</h1>
        
      </div>
      
    </div>
  );
}

export default WIP;