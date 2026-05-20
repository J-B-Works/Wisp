import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter, Routes, Route } from 'react-router-dom' 
import './index.css'

// Importando as duas páginas
import { Home } from './pages/Home/Home.jsx'
import { Login } from './pages/Login/Login.jsx' // <--- Puxando o seu arquivo de Login
import Feed from './pages/Feed/Feed.jsx'
import { Cadastro } from './pages/Cadastro/Cadastro.jsx'
import PerfilUC from './pages/PerfilUC/Perfil.jsx'
import { DetalhesAtividade } from './pages/DetalhesAtvdds/DetalhesAtividade.jsx'
import { WIP } from './pages/WIP/WIP.jsx';
import { Favoritos } from './pages/Favoritos/Favoritos.jsx';
import { EditarPerfil } from './pages/PerfilUC/EditarPerfil.jsx';

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/Login" element={<Login />} />
        <Route path="/feed" element={<Feed />} />
        <Route path="/cadastro" element={<Cadastro />} />
        <Route path="/perfiluc" element={<PerfilUC />} />
        <Route path="/detalhes" element={<DetalhesAtividade />} />
        <Route path="/em-breve" element={<WIP />} />
        <Route path="/favoritos" element={<Favoritos />} />
        <Route path="/editar-perfil" element={<EditarPerfil />} />
        
      </Routes>
    </BrowserRouter>
  </StrictMode>,
)