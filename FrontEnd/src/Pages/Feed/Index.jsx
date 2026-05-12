import './Style.css'; 
import { useNavigate } from 'react-router-dom';
import React, { useState, useEffect } from 'react'; // 1. Adicionado o useEffect aqui
import { NavbarPrincipal } from '../../assets/NavBar/navbar.jsx';

function Feed() {
  const navigate = useNavigate();

  // 2. CRIANDO A MEMÓRIA DO COMPONENTE
  // Trocamos o array fixo por um useState vazio. Ele vai guardar o JSON que vier da API.
  const [recomendacoes, setRecomendacoes] = useState([]);
  
  // Criamos uma variável para controlar quando a tela deve mostrar o "Carregando..."
  const [carregando, setCarregando] = useState(true);

  // 3. O TELEFONE DO REACT: useEffect
  // Esse bloco roda automaticamente assim que a página abre, indo buscar os dados
  useEffect(() => {
    const buscarDadosDaApi = async () => {
      try {
        const userID = localStorage.getItem('wisp_userId');
        // ==============================================================
        // QUANDO SUA API ESTIVER LIGADA, O CÓDIGO REAL SERÁ ESTE AQUI:
        const resposta = await fetch('http://localhost:8080/api/recomendacoes/' + userID);
        const dadosJson = await resposta.json();
        setRecomendacoes(dadosJson);
        setCarregando(false);
        // ==============================================================

        // POR ENQUANTO: Vamos fingir que a API demorou 1,5 segundos para responder
        //setTimeout(() => {
        //  const dadosDoBanco = [
        //    { id: 1, titulo: "Oficina de Python", local: "Fábrica Vila Nova", data: "12/05/2026", valor: "Gratuito", imagem: "https://images.unsplash.com/photo-1526379095098-d400fd0bfce8?auto=format&fit=crop&w=400&q=80"},
        //    { id: 2, titulo: "Curso de Redes", local: "SENAC Santana", data: "15/05/2026", valor: "R$ 150,00", imagem: "https://images.unsplash.com/photo-1544197150-b99a580bb7a8?auto=format&fit=crop&w=400&q=80" },
        //    { id: 3, titulo: "Exposição de Arte", local: "SESC Pompeia", data: "20/05/2026", valor: "Gratuito", imagem: "https://images.unsplash.com/photo-1536924940846-227afb31e2a5?auto=format&fit=crop&w=400&q=80"},
        //    { id: 4, titulo: "oie", local: "ensaios da Anitta", data: "25/02/2026", valor: "Pago", imagem: ""}
        //  ];
          
          //setRecomendacoes(dadosDoBanco); // Salva os dados
          //setCarregando(false); // Desliga a mensagem de carregamento
        //}, 1500);

      } catch (erro) {
        console.error("Erro ao conectar com a API:", erro);
        setCarregando(false);
      }
    };

    buscarDadosDaApi();
  }, []); // <-- O array vazio garante que a API só seja chamada UMA VEZ ao abrir a tela.


  return (
    <>
      <NavbarPrincipal mostrarBusca={true} />

      {/* 2. CONTEÚDO PRINCIPAL */}
      <div className="main-container" style={{ backgroundColor: 'var(--bg-bege)', minHeight: '100vh' }}>
        <h1 className="titulo-secao">Recomendações para você</h1>
        
        {/* TAGS COLORIDAS */}
        <div className="tags-container">
          <button className="tag" style={{ backgroundColor: 'var(--accent-laranja)', color: 'var(--bordas)' }}>Cursos</button>
          <button className="tag" style={{ backgroundColor: 'var(--accent-rosa)', color: 'var(--bordas)' }}>Oficinas</button>
          <button className="tag" style={{ backgroundColor: 'var(--accent-amarelo)', color: 'var(--bordas)' }}>Exposições</button>
          <button className="tag" style={{ backgroundColor: 'var(--accent-azul-claro)', color: 'var(--bordas)' }}>Apresentações</button>
        </div>

        {/* 4. A CONDICIONAL (IF/ELSE) NA TELA */}
        {/* Se 'carregando' for true, mostra a mensagem. Se for false, mostra o seu grid! */}
        {carregando ? (
          <h2 style={{ textAlign: 'center', marginTop: '50px' }}>⏳ Buscando atividades...</h2>
        ) : (
          
          /* GRID DE CARDS (Seu código original mantido 100% igual) */
          <div className="card-grid" style={{ gridTemplateColumns: 'repeat(auto-fill, minmax(260px, 1fr))' }}>
            {recomendacoes.map((item) => (
              <div className="activities-card" key={item.id} style={{ cursor: 'pointer' }} onClick={() => navigate(`/atividade/${item.id}`, { state: { atividade: item } })}>
                
                <div className="card-imagem" style={{ backgroundColor: 'var(--cinza-imagem)', borderBottom: '2px solid var(--bordas)', height: '220px', overflow: 'hidden' }}>
                  {/* A INTELIGÊNCIA: Verifica se a API mandou um link de imagem */}
                  {item.imagem ? (
                    <img 
                      src={item.imagem} 
                      alt={`Imagem da atividade: ${item.titulo}`} 
                      style={{ 
                        width: '100%', 
                        height: '100%', 
                        objectFit: 'cover' /* O SEGREDO: Corta a foto perfeitamente sem esticar ou achatar! */
                      }} 
                    />
                  ) : (
                    /* O PLANO B: Se não tiver imagem na API, desenha uma caixinha cinza com um ícone */
                    <div style={{ width: '100%', height: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#666', fontSize: '2rem' }}>
                      📷
                    </div>
                  )}

                </div>
                
                <div className="card-conteudo">
                  <div className="card-cabecalho">
                    <span>{item.titulo}</span>
                    <span style={{ fontSize: '1.5rem', cursor: 'pointer' }}>⭐</span>
                  </div>
                  <div className="card-info">📍 {item.local}</div>
                  <div className="card-info">📅 {item.data}</div>
                  <div className="card-info">💲 {item.valor}</div>
                </div>

              </div>
            ))}
          </div>

        )}
      </div>
    </>
  );
}

export default Feed;