import React, { useState } from 'react';
import './navbarstyle.css'; // ou style.css

// 1. PROPS: Adicionamos "{ mostrarBusca = true }" aqui. 
// Por padrão a barra aparece, mas podemos forçar ela a sumir.
export function NavbarPrincipal({ mostrarBusca = true }) {
  
  // 2. O ESTADO DA BUSCA: Aqui é onde o React guarda o que você digita
  const [termoBusca, setTermoBusca] = useState('');

  // Função para quando o usuário apertar Enter ou clicar na lupa (futuramente)
  const executarBusca = () => {
    console.log("O usuário pesquisou por:", termoBusca);
    // Aqui você vai mandar o termoBusca para o backend/API depois
  };

  return (
    <nav className="navbar" style={{ backgroundColor: 'var(--barra)', borderBottom: '2px solid var(--bordas)', position: 'relative', zIndex: 10 }}>
      
      {/* --- LOGO AQUI --- */}
      <div style={{ fontWeight: 'bold', fontSize: '1.2rem', backgroundColor: 'var(--accent-azul-claro)', padding: '10px 15px', borderRadius: '50px', border: '2px solid var(--bordas)' }}>
        Logo
      </div>

      {/* 3. RENDERIZAÇÃO CONDICIONAL: O React só desenha isso se mostrarBusca for true */}
      {mostrarBusca && (
        <div style={{ display: 'flex', gap: '15px', alignItems: 'center', flex: 1, justifyContent: 'center' }}>
          <div style={{ position: 'relative', width: '50%' }}>
            
            {/* 4. O INPUT FUNCIONAL (Componente Controlado) */}
            <input 
              type="text" 
              placeholder="Buscar Cursos, oficinas, exposições..." 
              value={termoBusca} /* O valor é amarrado à variável */
              onChange={(e) => setTermoBusca(e.target.value)} /* Atualiza a variável a cada letra digitada */
              onKeyDown={(e) => e.key === 'Enter' && executarBusca()} /* Permite buscar dando Enter */
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
      )}

        {/* Botões de Ação (Direita) */}
        <div style={{ display: 'flex', gap: '10px' }}>
          <button style={btnAcaoStyle('var(--accent-azul-claro)')}>⭐</button>
          <button style={btnAcaoStyle('var(--card)')}>⚙️</button>
          <button style={btnAcaoStyle('var(--accent-amarelo)')}>🍔</button>
        </div>

      {/* --- BOTÕES DA DIREITA AQUI --- */}
      <div style={{ display: 'flex', gap: '10px' }}>
        {/* ... seus botoes de estrela, config, etc ... */}
      </div>

    </nav>
  );
}

// Função auxiliar para os botões redondos do menu superior
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