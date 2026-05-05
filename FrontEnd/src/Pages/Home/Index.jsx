import React from 'react';
import '../../index.css'; // Garantindo que as variáveis globais (como --bg-bege e --bordas) sejam carregadas
import { useNavigate } from 'react-router-dom';

export function Home() {

  // 1. INICIANDO O HOOK DE NAVEGAÇÃO
  const navigate = useNavigate();

  // 2. SUBSTITUINDO OS CONSOLE.LOG PELO NAVEGADOR
  const irParaLogin = () => {
    navigate('/Login'); // Leva para a rota que configuramos no main.jsx
  };

  const irParaCadastro = () => {
    navigate('/Login'); // Como a sua tela tem ambas as opções, mandamos para o mesmo lugar
  };

  return (
    // Container principal: Ocupa a tela toda e centraliza os itens em coluna
    <div style={{ 
      backgroundColor: 'var(--bg-bege)', 
      minHeight: '100vh', 
      display: 'flex', 
      flexDirection: 'column', 
      alignItems: 'center', 
      justifyContent: 'center',
      textAlign: 'center',
      padding: '20px',
      gap: '20px', // Espaçamento geral entre os blocos
      fontFamily: 'sans-serif' // Caso não tenha uma fonte global definida ainda
    }}>

      {/* CABEÇALHO */}
      <div>
        <h1 style={{ color: '#1E5084', fontSize: '3.5rem', margin: '0 0 10px 0' }}>
          Wisp
        </h1>
        <h2 style={{ color: '#E87D2B', fontSize: '1.5rem', margin: '0' }}>
          Guia de Educação e Cultura
        </h2>
      </div>

      {/* PLACEHOLDER DO MASCOTE */}
      <div style={{
        width: '120px',
        height: '120px',
        backgroundColor: 'var(--accent-azul-claro)', // Fundo temporário
        border: '2px dashed var(--bordas)', // Borda tracejada indicando que é um placeholder
        borderRadius: '50%',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        fontSize: '3rem',
        margin: '10px 0'
      }}>
        👻 {/* Emoji provisório do macaquinho/fantasminha */}
      </div>

      {/* TEXTOS PRINCIPAIS */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '15px', maxWidth: '600px' }}>
        <p style={{ color: '#2C2B4B', fontWeight: 'bold', fontSize: '1.1rem', margin: '0' }}>
          Wisp é um espírito que guia viajantes até seus destinos
        </p>
        <p style={{ color: '#3B8B4A', fontWeight: 'bold', fontSize: '1.1rem', margin: '0' }}>
          E aqui,<br/>
          ele irá te guiar até atividades, cursos,<br/>
          exposições e oficinas que combinam com você!
        </p>
      </div>

      {/* ÁREA DOS BOTÕES */}
      <div style={{ display: 'flex', alignItems: 'center', gap: '15px', marginTop: '10px' }}>
        <button onClick={irParaLogin} style={{
          backgroundColor: '#165D9B',
          color: 'white',
          padding: '10px 30px',
          borderRadius: '8px',
          border: '2px solid var(--bordas)',
          boxShadow: '4px 4px 0px 0px var(--bordas)', // A sombra gordinha neobrutalista
          fontWeight: 'bold',
          fontSize: '1rem',
          cursor: 'pointer'
        }}>
          Entre
        </button>

        <span style={{ color: '#46A2C6', fontWeight: 'bold', fontSize: '1.1rem' }}>
          Ou
        </span>

        <button onClick={irParaCadastro} style={{
          backgroundColor: 'var(--accent-rosa)', // Usando a variável global rosa
          color: '#2C2B4B', // Cor escura para dar bom contraste na leitura
          padding: '10px 30px',
          borderRadius: '8px',
          border: '2px solid var(--bordas)',
          boxShadow: '4px 4px 0px 0px var(--bordas)',
          fontWeight: 'bold',
          fontSize: '1rem',
          cursor: 'pointer'
        }}>
          Cadastre-se
        </button>
      </div>

      {/* RODAPÉ COM CRÉDITOS */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '15px', marginTop: '10px' }}>
        <p style={{ color: '#46A2C6', fontWeight: 'bold', fontSize: '1.1rem', margin: '0' }}>
          Para uma experiência personalizada
        </p>
        
        <div style={{ color: '#C63F3F', fontWeight: 'bold', lineHeight: '1.4', fontSize: '0.95rem' }}>
          Por:<br/>
          Júlia Andrade - 10428513<br/>
          &amp;<br/>
          Bruna Gonçalves - 10425696
        </div>
      </div>

    </div>
  );
}

export default Home;