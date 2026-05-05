import React, { useState } from 'react';
import './navbarstyle.css'; 

// 1. PROPS: Adicionamos "mostrarHamburger" para controlar o 3º ícone
export function NavbarPrincipal({ mostrarBusca = true, mostrarHamburger = true }) {
  
  const [termoBusca, setTermoBusca] = useState('');

  const executarBusca = () => {
    console.log("O usuário pesquisou por:", termoBusca);
  };

  return (
    <nav className="navbar" style={{ backgroundColor: 'var(--barra)', borderBottom: '2px solid var(--bordas)', position: 'relative', zIndex: 10 }}>
      
      {/* --- LADO ESQUERDO: LOGO --- */}
      <div style={{ fontWeight: 'bold', fontSize: '1.2rem', backgroundColor: 'var(--accent-azul-claro)', padding: '10px 15px', borderRadius: '50px', border: '2px solid var(--bordas)' }}>
        Logo
      </div>

      {/* --- CENTRO: RENDERIZAÇÃO CONDICIONAL --- */}
      {/* Se mostrarBusca for true, desenha o input. Se for false, desenha o Título do Wisp! */}
      {mostrarBusca ? (
        <div style={{ display: 'flex', gap: '15px', alignItems: 'center', flex: 1, justifyContent: 'center' }}>
          <div style={{ position: 'relative', width: '50%' }}>
            
            <input 
              type="text" 
              placeholder="Buscar Cursos, oficinas, exposições..." 
              value={termoBusca} 
              onChange={(e) => setTermoBusca(e.target.value)} 
              onKeyDown={(e) => e.key === 'Enter' && executarBusca()} 
              style={{ 
                padding: '12px 20px', borderRadius: '50px', border: '2px solid var(--bordas)', 
                width: '100%', backgroundColor: 'var(--bg-bege)', outline: 'none'
              }}
            />
            
            <span 
              onClick={executarBusca} 
              style={{ position: 'absolute', right: '15px', top: '10px', fontSize: '1.2rem', cursor: 'pointer' }}
            >
              🔍
            </span>
          </div>
          
          <button className="tag" style={{ backgroundColor: 'var(--bg-bege)', display: 'flex', alignItems: 'center', gap: '5px' }}>
            <span>🔽</span> Filtros
          </button>
        </div>
      ) : (
        // O TÍTULO CENTRALIZADO (Aparece no Cadastro)
        <div style={{ flex: 1, textAlign: 'center', color: 'white', fontSize: '1.8rem', fontWeight: 'bold' }}>
          Wisp - Guia de Educação e Cultura
        </div>
      )}

      {/* --- LADO DIREITO: BOTÕES DE AÇÃO --- */}
      <div style={{ display: 'flex', gap: '10px' }}>
        <button style={btnAcaoStyle('var(--accent-azul-claro)')}>⭐</button>
        
        {/* Nota: Na sua imagem de referência não tem a engrenagem, apenas a estrela e o botão amarelo. 
            Se quiser esconder a engrenagem também no futuro, é só usar a mesma lógica do hambúrguer! */}
        <button style={btnAcaoStyle('var(--card)')}>⚙️</button>
        
        {/* 3. O ícone de Hambúrguer só aparece se a prop "mostrarHamburger" for true */}
        {mostrarHamburger && (
          <button style={btnAcaoStyle('var(--accent-amarelo)')}>🍔</button>
        )}
      </div>

    </nav>
  );
}

// Função auxiliar mantida intacta
const btnAcaoStyle = (cor) => ({
  backgroundColor: cor,
  border: '2px solid var(--bordas)',
  borderRadius: '50%',
  width: '45px',
  height: '45px',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  cursor: 'pointer',
  fontSize: '1.2rem'
});