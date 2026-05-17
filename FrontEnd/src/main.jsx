import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter, Routes, Route } from 'react-router-dom' 
import './index.css'

// Importando as duas páginas
import { Home } from './Pages/Home/Index.jsx'
import { Login } from './Pages/Login/Index.jsx' // <--- Puxando o seu arquivo de Login
import Feed from './Pages/Feed/Index.jsx'
import { Cadastro } from './Pages/Cadastro/Index.jsx'
import PerfilUC from './Pages/Perfil_UC/Index.jsx'
import { DetalhesAtividade } from './Pages/Detalhes_Atvdds/DetalhesAtividade.jsx'
import { WIP } from './Pages/WIP/WIP.jsx';
import { Favoritos } from './Pages/Favoritos/Favoritos.jsx';
import { EditarPerfil } from './Pages/Perfil_UC/Editar_perfil.jsx';

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/Login" element={<Login />} />
        <Route path="/feed" element={<Feed />} />
        <Route path="/cadastro" element={<Cadastro />} />
        <Route path="/perfiluc" element={<PerfilUC />} />
        <Route path="/atividade/:id" element={<DetalhesAtividade />} />
        <Route path="/em-breve" element={<WIP />} />
        <Route path="/favoritos" element={<Favoritos />} />
        <Route path="/editar-perfil" element={<EditarPerfil />} />
        
      </Routes>
    </BrowserRouter>
  </StrictMode>,
)